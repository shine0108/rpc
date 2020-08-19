package com.scalahome.test;


import com.scalahome.rpc.Async;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author fuqing.xu
 * @date 2020-07-14 14:08
 */
@Slf4j
public class IRPCTestImpl implements IRPCTest {

    @Override
    public String ping0(String msg) {
//        log.info("ping0:" + msg);
        return "pang0:" + msg;
    }

    @Override
    public void ping1(String msg) {
        log.info("ping1:" + msg);
    }

    @Override
    public void ping2() {
        log.info("ping2");
    }

    @Async
    @Override
    public void ping3() {
        log.info("ping3");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    private AtomicLong count = new AtomicLong();

    @Async
    @Override
    public void ping4(String msg) {
        long c = count.incrementAndGet();
        if(c % 10000 ==0 ) {
            log.info("ping4:" + c);
        }
//        log.info("ping4:" + msg);
    }

    @Override
    public String ping5(String msg, Object obj) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        return "pang5:" + obj;
    }
}
