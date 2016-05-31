package com.scalahome.rpc;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by xufuqing on 16/5/16.
 */
public class NettyClient implements TCPClient {

    private Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private Channel channel;

    @Override
    public void connect(String host, int port) throws InterruptedException {
        if (channel == null) {
            synchronized (this) {
                if (channel == null) {
                    Bootstrap bootstrap = new Bootstrap();
                    bootstrap.group(new NioEventLoopGroup());
                    bootstrap.channel(NioSocketChannel.class);
                    bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                            pipeline.addLast("decoder", new ByteArrayDecoder());
                            pipeline.addLast("encoder", new ByteArrayEncoder());
                            pipeline.addLast(new TcpClientHandler());
                        }
                    });
                    bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
                    channel = bootstrap.connect(host, port).sync().channel();
                }
            }
        }
    }

    @Override
    public void sendAsync(byte[] data) {
        try {
            byte[] wrappedData = new byte[data.length + 1];
            System.arraycopy(data, 0, wrappedData, 1, data.length);
            channel.writeAndFlush(wrappedData).sync();
        } catch (InterruptedException e) {
            logger.error(IOUtils.getStackTrace(e));
        }
    }

    private Map<UUID, WrappedAck> waitAck = new ConcurrentHashMap<UUID, WrappedAck>();

    @Override
    public byte[] sendSync(byte[] data, long timeout) throws InterruptedException, RemoteException {
        byte[] wrappedData = new byte[data.length + 1 + 16];
        wrappedData[0] = 1;
        UUID uuid = UUID.randomUUID();
        System.arraycopy(IOUtils.longToByteArray(uuid.getMostSignificantBits()), 0, wrappedData, 1, 8);
        System.arraycopy(IOUtils.longToByteArray(uuid.getLeastSignificantBits()), 0, wrappedData, 1 + 8, 8);
        System.arraycopy(data, 0, wrappedData, 1 + 16, data.length);
        channel.writeAndFlush(wrappedData).sync();
        WrappedAck wrappedAck;
        synchronized (uuid) {
            try {
                waitAck.put(uuid, new WrappedAck(uuid));
                uuid.wait(timeout);
            } finally {
                wrappedAck = waitAck.remove(uuid);
            }
        }
        if (wrappedAck.responseCode == 0) {
            return wrappedAck.ack;
        } else {
            throw new RemoteException("Error With ResponseCode:" + wrappedAck.responseCode);
        }
    }

    private class TcpClientHandler extends SimpleChannelInboundHandler<byte[]> {

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, byte[] data) throws Exception {
            if (data != null && data.length >= 17) {
                UUID uuid = new UUID(IOUtils.byteArrayToLong(data, 0), IOUtils.byteArrayToLong(data, 8));
                WrappedAck wrappedAck = waitAck.get(uuid);
                if (wrappedAck != null) {
                    wrappedAck.responseCode = data[16];
                    wrappedAck.ack = new byte[data.length - 16 - 1];
                    System.arraycopy(data, 16 + 1, wrappedAck.ack, 0, data.length - 16 - 1);
                    synchronized (wrappedAck.uuid) {
                        wrappedAck.uuid.notifyAll();
                    }
                }
            }
        }
    }

    private class WrappedAck {
        WrappedAck(UUID uuid) {
            this.uuid = uuid;
        }

        UUID uuid;
        byte[] ack;
        byte responseCode;
    }
}
