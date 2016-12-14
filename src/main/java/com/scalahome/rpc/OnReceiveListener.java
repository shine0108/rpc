package com.scalahome.rpc;

import com.scalahome.rpc.Message;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by fuqing.xfq on 2016/12/5.
 */
public interface OnReceiveListener {
    void onReceive(ChannelHandlerContext ctx, Message message);
}
