package com.scalahome.rpc;

/**
 * Created by xufuqing on 16/5/16.
 */
public interface Serializer {
    <T> byte[] serialize(Class<T> clazz, T t);

    <T> T deSerialize(Class<T> clazz, byte[] data) throws ReflectiveOperationException;
}
