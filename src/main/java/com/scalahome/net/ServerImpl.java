package com.scalahome.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author fuqing.xu
 * @date 2020-07-08 15:05
 */
@Slf4j
public class ServerImpl implements Server {

    private final InetSocketAddress localAddress;
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;
    private OnReadListener onReadListener;
    private ExecutorService threadPool;
    private Map<String, Object> services = new ConcurrentHashMap<>();

    public ServerImpl(InetSocketAddress localAddress) {
        this.localAddress = localAddress;
    }

    @Override
    public synchronized void start() throws IOException {
        if (bossGroup != null || workerGroup != null) {
            throw new IOException("Cant Start Twice!");
        }
        if (onReadListener == null) {
            throw new IOException("OnReadListener Cant Be Null!");
        }
        threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors() * 2,
                10 * 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(1024 * 1024),
                new DefaultThreadFactory(ServerImpl.class.getName()));
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, false)
                .option(ChannelOption.SO_SNDBUF, 1024 * 1024)
                .option(ChannelOption.SO_RCVBUF, 1024 * 1024)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .option(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(8 * 1024 * 1024, 16 * 1024 * 1024))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast("FrameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
                                .addLast("FrameEncoder", new LengthFieldPrepender(4))
                                .addLast("Decoder", new ByteArrayDecoder())
                                .addLast("Encoder", new ByteArrayEncoder())
                                .addLast(new ServerHandler());
                    }
                });
        try {
            serverBootstrap.bind(localAddress.getPort()).sync();
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
        log.info("bind success:" + localAddress.getPort());
    }

    @Override
    public synchronized void close() throws IOException {
        if (bossGroup != null) {
            try {
                bossGroup.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                throw new IOException(e);
            }
            bossGroup = null;
        }
        if (workerGroup != null) {
            try {
                workerGroup.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                throw new IOException(e);
            }
            workerGroup = null;
        }
        if (threadPool != null) {
            threadPool.shutdown();
            threadPool = null;
        }
        log.info("shutdown success");
    }

    @Override
    public void setOnReadListener(OnReadListener onReadListener) {
        if (onReadListener == null) {
            throw new NullPointerException("OnReadListener Cant Be Null!");
        }
        this.onReadListener = onReadListener;
    }

    @Override
    public void addService(String serviceName, Object service) {
        services.put(serviceName, service);
    }

    @Override
    public Object getService(String serviceName) {
        return services.get(serviceName);
    }

    @Override
    public Object removeService(String serviceName) {
        return services.remove(serviceName);
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return localAddress;
    }

    class ServerHandler extends SimpleChannelInboundHandler<byte[]> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        byte[] data = onReadListener.onRead(msg, ctx);
                        if (data != null) {
                            ctx.channel().writeAndFlush(data);
                        }
                    } catch (Throwable e) {
                        log.error(e.getMessage(), e);
                    }
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
    }
}
