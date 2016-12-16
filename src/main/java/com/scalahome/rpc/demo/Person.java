package com.scalahome.rpc.demo;

import com.scalahome.rpc.Sync;
import com.scalahome.rpc.VersionedProtocol;

/**
 * Created by xufuqing on 16/5/31.
 */
public interface Person extends VersionedProtocol{
    @Sync
    String getName();
    @Sync
    void setName(String name);
    @Sync
    void setAge(int age);
    @Sync
    int getAge();

}
