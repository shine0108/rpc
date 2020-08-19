package com.scalahome.net;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author fuqing.xu
 * @date 2020-07-08 15:04
 */
public interface Client extends Closeable {
    void connect(InetSocketAddress remoteAddress) throws IOException;

    void addOnReadListener(OnReadListener onReadListener);

    void sendMsg(byte[] msg) throws IOException;
}
