package com.scalahome.ds;

import com.scalahome.rpc.tuple.Tuple_2;

import java.util.Set;

/**
 * Created by fuqing.xfq on 2016/12/16.
 */
public interface MetaDataNode {
    Tuple_2<String, String> getKeyRange();
    Set<String> getLocations(String key);
    void addLocation(String key, String location);
    void removeLocation(String key, String location);
}
