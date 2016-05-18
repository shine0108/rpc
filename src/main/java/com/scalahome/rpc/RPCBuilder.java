package com.scalahome.rpc;

/**
 * Created by xufuqing on 16/5/16.
 */
public interface RPCBuilder {
    <T> T getProxy(Class<T> clazz, long versionID, String host, int port, long timeout) throws InterruptedException;

    <T> void startServer(T t, long version, String host, int port) throws InterruptedException;
}
