package com.scalahome.rpc;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by xufuqing on 16/5/16.
 */
public class NettyServer implements TCPServer {

    private Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private OnReceiveListener onReceiveListener;

    @Override
    public void start(String host, int port) throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(new NioEventLoopGroup(Runtime.getRuntime().availableProcessors()),
                new NioEventLoopGroup(Runtime.getRuntime().availableProcessors()));
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                pipeline.addLast("decoder", new ByteArrayDecoder());
                pipeline.addLast("encoder", new ByteArrayEncoder());
                pipeline.addLast(new TcpServerHandler(NettyServer.this));
            }
        });
        serverBootstrap.bind(host, port).sync();
    }

    @Override
    public void setOnReceiveListener(OnReceiveListener onReceiveListener) {
        this.onReceiveListener = onReceiveListener;
    }

    private class TcpServerHandler extends SimpleChannelInboundHandler<byte[]> {

        private final NettyServer nettyServer;

        private TcpServerHandler(NettyServer nettyServer) {
            this.nettyServer = nettyServer;
        }

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, byte[] data) throws Exception {
            if (data[0] == 0) {
                byte[] realData = new byte[data.length - 1];
                System.arraycopy(data, 1, realData, 0, realData.length);
                nettyServer.onReceiveListener.onReceive(realData);
            } else if (data[0] == 1) {
                byte[] realData = new byte[data.length - 1 - 16];
                System.arraycopy(data, 1 + 16, realData, 0, realData.length);
                byte[] ack = nettyServer.onReceiveListener.onReceive(realData);
                byte[] wrappedAck = new byte[(ack == null ? 0 : ack.length) + 16 + 1];
                System.arraycopy(data, 1, wrappedAck, 0, 16);
                if (ack == null) {
                    wrappedAck[16] = 1;
                } else {
                    System.arraycopy(ack, 0, wrappedAck, 17, ack.length);
                }
                channelHandlerContext.channel().writeAndFlush(wrappedAck).sync();
            }
        }
    }
}
