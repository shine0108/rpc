package com.scalahome.rpc;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Created by xufuqing on 16/5/16.
 */
public class App {

    private static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws InterruptedException, ReflectiveOperationException {
        testRpc();
    }

    private static void testRpc() throws InterruptedException {
        RPCBuilder rpcBuilder = new DefaultRPCBuilder();
        rpcBuilder.startServer(new PersonImpl(), 1L, "localhost", 10000);
        Person person = rpcBuilder.getProxy(Person.class, 1L, "localhost", 10000, 100);
        person.setAge(3, 234);
        String name = person.getName();
        logger.info("get name:" + name);
    }


}
