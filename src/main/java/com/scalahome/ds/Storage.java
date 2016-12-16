package com.scalahome.ds;

/**
 * Created by fuqing.xfq on 2016/12/16.
 */
public interface Storage {
    void put(String key, byte[] data);
    byte[] get(String key);
}
