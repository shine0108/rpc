package com.scalahome.net;

import io.netty.channel.ChannelHandlerContext;

public interface OnReadListener {
    byte[] onRead(byte[] msg, ChannelHandlerContext ctx);
    default void onChannelInactive() {}
}