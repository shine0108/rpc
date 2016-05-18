package com.scalahome.rpc;

/**
 * Created by xufuqing on 16/5/16.
 */
public interface TCPClient {
    void connect(String host, int port) throws InterruptedException;
    void sendAsync(byte[] data);
    byte[] sendSync(byte[] data, long timeout) throws InterruptedException, RemoteException;
}