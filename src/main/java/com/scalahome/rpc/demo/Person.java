package com.scalahome.rpc.demo;

import com.scalahome.rpc.Async;

/**
 * Created by xufuqing on 16/5/18.
 */
public interface Person {
    @Async
    void setAge(int age, int value);
    String getName();
}
