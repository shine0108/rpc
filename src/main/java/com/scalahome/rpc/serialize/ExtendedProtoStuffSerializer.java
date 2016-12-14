package com.scalahome.rpc.serialize;

import com.scalahome.rpc.utils.IOUtils;

/**
 * Created by xufuqing on 16/5/18.
 */
public class ExtendedProtoStuffSerializer extends ProtoStuffSerializer {
    @Override
    public <T> byte[] serialize(Class<T> clazz, T t) {
        if(byte[].class == clazz) {
            return (byte[]) t;
        } else if (byte.class == clazz) {
            return new byte[]{(Byte)t};
        } else if (short.class == clazz) {
            return IOUtils.shortToByteArray((Short) t);
        } else if (int.class == clazz) {
            return IOUtils.intToByteArray((Integer) t);
        } else if (long.class == clazz) {
            return IOUtils.longToByteArray((Long) t);
        } else if (float.class == clazz) {
            long value = Double.doubleToLongBits((Float) t);
            return IOUtils.longToByteArray(value);
        } else if (double.class == clazz) {
            long value = Double.doubleToLongBits((Double) t);
            return IOUtils.longToByteArray(value);
        } else if (boolean.class == clazz) {
            return new byte[]{(byte) ((Boolean) t ? 1 : 0)};
        } else if (char.class == clazz) {
            return IOUtils.intToByteArray((Character) t);
        } else {
            return super.serialize(clazz, t);
        }
    }

    @Override
    public <T> T deSerialize(Class<T> clazz, byte[] data) throws InstantiationException, IllegalAccessException {
        if(byte[].class == clazz) {
            return (T) data;
        } else if (byte.class == clazz) {
            return (T)(Byte)data[0];
        } else if (short.class == clazz) {
            return (T)(Short)IOUtils.byteArrayToShort(data, 0);
        } else if (int.class == clazz) {
            return (T)(Integer)IOUtils.byteArrayToInt(data, 0);
        } else if (long.class == clazz) {
            return (T)(Long)IOUtils.byteArrayToLong(data, 0);
        } else if (float.class == clazz) {
            return (T)(Float)(float)Double.longBitsToDouble(IOUtils.byteArrayToLong(data, 0));
        } else if (double.class == clazz) {
            return (T)(Double)Double.longBitsToDouble(IOUtils.byteArrayToLong(data, 0));
        } else if (boolean.class == clazz) {
            return (T)(Boolean)(data[0] == 1);
        } else if (char.class == clazz) {
            return (T)(Character)(char)IOUtils.byteArrayToInt(data, 0);
        } else {
            return super.deSerialize(clazz, data);
        }
    }
}
