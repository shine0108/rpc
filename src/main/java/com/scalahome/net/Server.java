package com.scalahome.net;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author fuqing.xu
 * @date 2020-07-08 15:04
 */
public interface Server extends Closeable {
    void start() throws IOException;

    void setOnReadListener(OnReadListener onReadListener);

    void addService(String serviceName, Object service);

    Object getService(String serviceName);

    Object removeService(String serviceName);

    InetSocketAddress getLocalAddress();
}
