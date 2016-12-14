package com.scalahome.rpc.serialize;

/**
 * Created by xufuqing on 16/5/16.
 */
public interface RPCSerializer {
    <T> byte[] serialize(Class<T> clazz, T t);

    <T> T deSerialize(Class<T> clazz, byte[] data) throws IllegalAccessException, InstantiationException;
}
