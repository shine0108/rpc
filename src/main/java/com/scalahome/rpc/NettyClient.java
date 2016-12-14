package com.scalahome.rpc;

import com.scalahome.rpc.serialize.RPCSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

/**
 * Created by fuqing.xfq on 2016/12/5.
 */
public class NettyClient implements Client {

    private Bootstrap bootstrap;
    private OnReceiveListener listener;
    private Channel channel;

    {
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline()
                    .addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
                    .addLast("frameEncoder", new LengthFieldPrepender(4))
                    .addLast("decoder", new ByteArrayDecoder())
                    .addLast("encoder", new ByteArrayEncoder())
                    .addLast(new ClientHandler());
            }
        });
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
    }

    @Override
    public void setOnReceiveListener(OnReceiveListener listener) {
        this.listener = listener;
    }

    class ClientHandler extends SimpleChannelInboundHandler<byte[]> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
            if(listener != null) {
                RPCSerializer serializer = RPCFactory.getInstance().getSerializer();
                Message message = serializer.deSerialize(Message.class, msg);
                listener.onReceive(ctx, message);
            }
        }
    }

    @Override
    public void connect(String host, int port) throws InterruptedException {
        this.channel =  bootstrap.connect(host, port).sync().channel();
    }

    @Override
    public void sendMsg(Message message) {
        RPCSerializer serializer = RPCFactory.getInstance().getSerializer();
        byte[] data = serializer.serialize(Message.class, message);
        channel.writeAndFlush(data);
    }

}
