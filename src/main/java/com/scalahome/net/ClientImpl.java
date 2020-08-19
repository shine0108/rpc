package com.scalahome.net;


import com.scalahome.common.CopyOnWriteSet;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author fuqing.xu
 * @date 2020-07-08 15:20
 */
@Slf4j
public class ClientImpl implements Client {
    private final int connectTimeout;
    private final ExecutorService threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() * 2,
            10 * 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(1024 * 1024),
            new DefaultThreadFactory(ClientImpl.class.getName(), true));
    private Set<OnReadListener> onReadListeners = new CopyOnWriteSet<>();
    private EventLoopGroup group;
    private Channel channel;
    private Bootstrap bootstrap;
    private InetSocketAddress remoteAddress;
    private volatile boolean isClosed = true;

    public ClientImpl(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    @Override
    public synchronized void connect(@NonNull InetSocketAddress remoteAddress) throws IOException {
        if (group != null) {
            throw new IOException("Cant Connect Twice!");
        }
        this.remoteAddress = remoteAddress;
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, false)
                .option(ChannelOption.SO_SNDBUF, 1024 * 1024)
                .option(ChannelOption.SO_RCVBUF, 1024 * 1024)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
                .option(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(8 * 1024 * 1024, 16 * 1024 * 1024))
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline()
                                .addLast("FrameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
                                .addLast("FrameEncoder", new LengthFieldPrepender(4))
                                .addLast("Decoder", new ByteArrayDecoder())
                                .addLast("Encoder", new ByteArrayEncoder())
                                .addLast(new ClientHandler());
                    }
                });
        try {
            channel = bootstrap.connect(remoteAddress).sync().channel();
        } catch (Exception e) {
            close();
            throw new IOException(e);
        }
        isClosed = false;
    }

    @Override
    public synchronized void close() throws IOException {
        isClosed = true;
        doClose(true);
    }

    public void doClose(boolean shutdownGroup) throws IOException {
        log.info("close ...");
        try {
            if (channel != null) {
                try {
                    channel.close().sync();
                } catch (InterruptedException e) {
                    throw new IOException(e);
                }
                channel = null;
            }
        } finally {
            if (shutdownGroup && group != null) {
                try {
                    group.shutdownGracefully().sync();
                } catch (InterruptedException e) {
                    throw new IOException(e);
                }
                group = null;
            }
        }
        log.info("close success");
    }

    @Override
    public void addOnReadListener(OnReadListener onReadListener) {
        if (onReadListener == null) {
            throw new NullPointerException("OnReadListener Cant Be Null!");
        }
        onReadListeners.add(onReadListener);
    }

    @Override
    public void sendMsg(byte[] msg) throws IOException {
        if (isClosed) {
            throw new SocketException("Channel Closed");
        } else if (channel == null || !channel.isOpen() || !channel.isActive() || !channel.isRegistered()) {
            if (!reconnect(-1)) {
                throw new SocketException("No Available Connect");
            }
        }
        while (!channel.isWritable()) {
            Thread.yield();
        }
        channel.writeAndFlush(msg);
    }

    class ClientHandler extends SimpleChannelInboundHandler<byte[]> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    for (OnReadListener onReadListener : onReadListeners) {
                        try {
                            if (onReadListener.onRead(msg, ctx) != null) {
                                return;
                            }
                        } catch (Throwable e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                    log.warn("Not Handled Msg:" + ctx.channel().remoteAddress() + "," + Hex.encodeHexString(msg));
                }
            });
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            log.error("exceptionCaught", cause);
            super.exceptionCaught(ctx, cause);
            log.warn("channel will be closed due to:" + cause);
            ctx.channel().close();
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            log.warn("channelInactive:" + ctx.channel().remoteAddress());
            for (OnReadListener onReadListener : onReadListeners) {
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            onReadListener.onChannelInactive();
                        } catch (Throwable e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                });
            }
            if (!isClosed) {
                tryReconnect(3);
            } else {
                log.info("channelInactiveDueToClose");
            }
            super.channelInactive(ctx);
        }
    }

    private final Lock tryReconnectLock = new ReentrantLock();
//    private final int MAX_RETRY_TIMES = 3;

    public void tryReconnect(int maxRetryTimes) {
        if (isClosed) {
            return;
        }
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                if (!isClosed && tryReconnectLock.tryLock()) {
                    try {
                        int retryTimes = 0;
                        while (!isClosed
                                && (channel == null || !channel.isOpen() || !channel.isActive() || !channel.isRegistered())
                                && retryTimes < maxRetryTimes
                                && !reconnect(retryTimes)) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e1) {
                                log.error(e1.getMessage(), e1);
                            }
                            retryTimes++;
                        }
                    } catch (Throwable e) {
                        log.error(e.getMessage(), e);
                    } finally {
                        tryReconnectLock.unlock();
                    }
                }
            }
        });
    }

    public boolean reconnect(int retryTimes) {
        log.info("Reconnecting:" + retryTimes);
        try {
            try {
                doClose(false);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            channel = bootstrap.connect(remoteAddress).sync().channel();
            log.info("Reconnect Success:" + retryTimes);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.info("Reconnect Failed:" + retryTimes);
            return false;
        }
    }
}
