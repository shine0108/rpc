package com.scalahome.common.serialize;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author fuqing.xu
 * @date 2020-07-30 18:18
 */
@Slf4j
public class JsonCommonSerializer implements CommonSerializer {

    private static JsonMapper mapper = JsonMapper.builder().build();

    @Override
    public <T> byte[] serialize(Class<T> clazz, T t) {
        String str = serialize(t);
        return str == null ? null : str.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public <T> T deSerialize(Class<T> clazz, byte[] data, int offset, int length) throws IOException {
        return mapper.readValue(data, offset, length, clazz);
    }

    public <T> String serialize(T t) {
        try {
            return mapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public <T> T deSerialize(Class<T> clazz, String str) throws IOException {
        byte[] data = str.getBytes(StandardCharsets.UTF_8);
        return deSerialize(clazz, data, 0, data.length);
    }
}
