package com.scalahome.rpc.demo;

import com.scalahome.rpc.Sync;
import com.scalahome.rpc.VersionedProtocol;

import java.util.Map;

/**
 * Created by xufuqing on 16/5/31.
 */
public interface Person extends VersionedProtocol{
    String getName();
    @Sync
    void setName(String name);
    @Sync
    void setAge(int age);
    int getAge();

    void setAttribute(Map<String, String> msg);

}
