package com.scalahome.common.serialize;

/**
 * @author fuqing.xu
 * @date 2020-07-24 20:43
 */
public class CommonSerializerFactory {
    public static CommonSerializer newCommonSerializer() {
        return new ProtoStuffSerializer();
    }
}
