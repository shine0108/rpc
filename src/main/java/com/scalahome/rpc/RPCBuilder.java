package com.scalahome.rpc;

/**
 * Created by xufuqing on 16/5/16.
 */
public interface RPCBuilder {
    <T> T getProxy(Class<T> clazz, long versionID, String host, int port, long timeout);

    <T extends VersionedProtocol> TCPServer startServer(T t, String host, int port) throws InterruptedException;
}