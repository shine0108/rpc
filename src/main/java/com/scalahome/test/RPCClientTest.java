package com.scalahome.test;

import com.scalahome.common.SnowFlake;
import com.scalahome.rpc.RPCProxyPool;
import com.scalahome.rpc.erros.RPCException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;

import java.net.InetSocketAddress;
import java.security.SecureRandom;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author fuqing.xu
 * @date 2020-07-14 14:12
 */
@Slf4j
public class RPCClientTest {
    public static void main(String[] args) throws ExecutionException, RPCException {
        RPCProxyPool rpcProxyPool = new RPCProxyPool(1600, TimeUnit.SECONDS, null, SnowFlake.getInstance(), 1000 * 3);
        InetSocketAddress inetSocketAddress = new InetSocketAddress("10.128.15.161", 9090);
        IRPCTest irpcTest = rpcProxyPool.getProxy(IRPCTest.class, inetSocketAddress);
        log.info("get proxy success");
        log.info("ping0:" + irpcTest.ping0("ping0"));
        try {
            irpcTest.ping1("ping1");
            irpcTest.ping2();
            irpcTest.ping3();
            irpcTest.ping4("ping4");
            log.info("ping5:" + irpcTest.ping5("ping5", 123));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        SecureRandom secureRandom = new SecureRandom();
        byte[] data = new byte[1024];
        secureRandom.nextBytes(data);
        String msg = Hex.encodeHexString(data);
        ExecutorService threadPool = Executors.newCachedThreadPool();
        for (int n = 0; n < 10; n++) {
            final int index = n;
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        long start = System.currentTimeMillis();
                        for (int i = 0; i <= 1000000; i++) {
                            String response = irpcTest.ping0("ping0:" + i + "," + index + "," + msg);
                            if (i % 10000 == 0) {
                                log.info(response.substring(0, 20));
                            }
                        }
                        log.info("time:" + (System.currentTimeMillis() - start));
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            });
        }


    }
}
