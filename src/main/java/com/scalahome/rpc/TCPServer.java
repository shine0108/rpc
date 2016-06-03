package com.scalahome.rpc;

/**
 * Created by xufuqing on 16/5/16.
 */
public interface TCPServer {
    void start(String host, int port) throws InterruptedException;
    void setOnReceiveListener(OnReceiveListener onReceiveListener);
    void stop();

    interface OnReceiveListener {
        byte[] onReceive(byte[] data);
    }
}
