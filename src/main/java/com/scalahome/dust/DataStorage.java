package com.scalahome.dust;

/**
 * Created by fuqing.xfq on 2016/12/16.
 */
public interface DataStorage {
    void put(String key, byte[] data);
    byte[] get(String key);
    void remove(String key);
}
