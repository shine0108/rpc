package com.scalahome.rpc.demo;

import com.scalahome.rpc.DefaultRPCBuilder;
import com.scalahome.rpc.RPCBuilder;
import com.scalahome.rpc.TCPServer;
import org.apache.log4j.Logger;
import org.apache.tools.ant.util.LinkedHashtable;

import java.util.LinkedHashMap;

/**
 * Created by xufuqing on 16/5/16.
 */
public class App {

    private static Logger logger = Logger.getLogger(App.class);

    public static void main(String[] args) throws InterruptedException {
        RPCBuilder rpcBuilder = new DefaultRPCBuilder();
        TCPServer tcpServer = rpcBuilder.startServer(new PersonImpl(), "localhost", 10000);
        Person proxy = rpcBuilder.getProxy(Person.class, 1L, "localhost", 10000, 1000);
        proxy.setName("Rose");
        logger.info(proxy.getName());
        proxy.setName("Jack");
        logger.info(proxy.getName());
//        tcpServer.stop();
        System.gc();
        logger.info("first gc");
        proxy = null;
        System.gc();
        logger.info("second gc");
//        logger.info(proxy.getName());
//        proxy.setName("Tom");

        LinkedHashMap linkedHashMap;


    }

}
