package com.scalahome.rpc.demo;

import com.scalahome.rpc.VersionedProtocol;

/**
 * Created by xufuqing on 16/5/31.
 */
public interface Person extends VersionedProtocol{
    String getName();
    void setName(String name);
}
