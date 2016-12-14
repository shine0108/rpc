package com.scalahome.rpc;

import com.scalahome.rpc.Message;
import com.scalahome.rpc.OnReceiveListener;
import com.scalahome.rpc.RPCFactory;
import com.scalahome.rpc.Server;
import com.scalahome.rpc.serialize.RPCSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import org.apache.log4j.Logger;

/**
 * Created by fuqing.xfq on 2016/12/5.
 */
public class NettyServer implements Server {

    private String host;
    private int port;

    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    private Logger logger = Logger.getLogger(NettyServer.class);
    private OnReceiveListener listener;

    @Override
    public void start(String host, int port) throws InterruptedException {
        this.host = host;
        this.port = port;
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast("FrameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
                                .addLast("FrameEncoder", new LengthFieldPrepender(4))
                                .addLast("decoder", new ByteArrayDecoder())
                                .addLast("encoder", new ByteArrayEncoder())
                                .addLast(new ServerHandler());
                    }
                });
        bootstrap.bind(host, port).sync();
        logger.info("Server Bind Success, host:" + host + ",port:" + port);
    }

    @Override
    public void shutdown() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

    @Override
    public void setOnReceiveListener(OnReceiveListener listener) {
        this.listener = listener;
    }

    class ServerHandler extends SimpleChannelInboundHandler<byte[]> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
            if(listener != null) {
                RPCSerializer serializer = RPCFactory.getInstance().getSerializer();
                Message message = serializer.deSerialize(Message.class, msg);
                listener.onReceive(ctx, message);
            }
        }
    }
}
