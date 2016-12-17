package com.scalahome.dust;

import java.util.Set;

/**
 * Created by fuqing.xfq on 2016/12/17.
 */
public interface NodeServer {
    Set<String> getLocations(String key);
    void put(String key, byte[] data, int targetReplication);
    void setReplication(String key, int targetReplication);
    byte[] get(String key);
    void remove(String key);
}
