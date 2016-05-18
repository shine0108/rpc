package com.scalahome.rpc;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

/**
 * Created by xufuqing on 16/5/16.
 */
public class ProtoStuffSerializer implements Serializer {

    @Override
    public <T> byte[] serialize(Class<T> clazz, T t) {
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        return ProtostuffIOUtil.toByteArray(t, schema, buffer);
    }

    @Override
    public <T> T deSerialize(Class<T> clazz, byte[] data) throws ReflectiveOperationException {
        T t = clazz.newInstance();
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        ProtostuffIOUtil.mergeFrom(data, t, schema);
        return t;
    }
}
