package com.scalahome.test;

import com.scalahome.rpc.Async;

/**
 * @author fuqing.xu
 * @date 2020-07-14 14:07
 */
public interface IRPCTest {
    String ping0(String msg);
    void ping1(String msg);
    void ping2();
    @Async
    void ping3();
    @Async
    void ping4(String msg);
    String ping5(String msg, Object obj);
}
