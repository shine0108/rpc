package com.scalahome.dust;

import com.scalahome.tuple.Tuple_2;

import java.util.Set;

/**
 * Created by fuqing.xfq on 2016/12/16.
 */
public interface MetaDataNode {
    Tuple_2<String, String> getKeyRange();
    void reportLocation(String key, String location, int targetReplication);
    Set<String> getLocations(String key);
    void addLocation(String key, String location);
    void removeLocation(String key, String location);
    void removeKey(String key);
}
