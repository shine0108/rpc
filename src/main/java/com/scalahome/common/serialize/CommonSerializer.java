package com.scalahome.common.serialize;

import java.io.IOException;

public interface CommonSerializer {
    <T> byte[] serialize(Class<T> clazz, T t);

    <T> T deSerialize(Class<T> clazz, byte[] data, int offset, int length) throws IOException;
}