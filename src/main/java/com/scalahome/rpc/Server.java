package com.scalahome.rpc;

/**
 * Created by fuqing.xfq on 2016/12/5.
 */
public interface Server {
    void start(String host, int port) throws InterruptedException;
    void shutdown();
    void setOnReceiveListener(OnReceiveListener listener);
}
