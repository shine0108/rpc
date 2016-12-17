package com.scalahome.dust;

/**
 * Created by fuqing.xfq on 2016/12/16.
 */
public interface DataNode {
    void put(String key, byte[] data);
    byte[] get(String key);
    boolean contains(String key);
    void remove(String key);
}
