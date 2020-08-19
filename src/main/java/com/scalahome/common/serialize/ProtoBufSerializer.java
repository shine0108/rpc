package com.scalahome.common.serialize;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ProtoBufSerializer implements CommonSerializer {

    @Override
    public <T> byte[] serialize(Class<T> clazz, T t) {
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        return ProtobufIOUtil.toByteArray(t, schema, buffer);
    }

    @Override
    public <T> T deSerialize(Class<T> clazz, byte[] data, int offset, int length) throws IOException {
        T t = null;
        try {
            t = clazz.newInstance();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new IOException(e);
        }
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        ProtobufIOUtil.mergeFrom(data, offset, length, t, schema);
        return t;
    }
}