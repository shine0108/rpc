package com.scalahome.rpc;

/**
 * Created by fuqing.xfq on 2016/12/5.
 */
public interface Client {
    void connect(String host, int port) throws InterruptedException;
    void sendMsg(Message msg) throws InterruptedException;
    void setOnReceiveListener(OnReceiveListener listener);
}
