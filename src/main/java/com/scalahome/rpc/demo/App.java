package com.scalahome.rpc.demo;

import com.scalahome.rpc.RPCBuilder;
import com.scalahome.rpc.RPCFactory;
import com.scalahome.rpc.Server;
import org.apache.log4j.Logger;

import java.util.BitSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by xufuqing on 16/5/16.
 */
public class App {

    private static Logger logger = Logger.getLogger(App.class);

    public static void main(String[] args) throws InterruptedException {
        RPCBuilder rpcBuilder = RPCFactory.getInstance().getRpcBuilder();
        Server server = rpcBuilder.startServer(new PersonImpl(), "127.0.0.1", 9090);

        new Thread(){
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    RPCBuilder rpcBuilder = RPCFactory.getInstance().getRpcBuilder();
                    Person person = rpcBuilder.getProxy(Person.class, 1, "127.0.0.1", 9090, 100);
                    person.setName("tom");
                    logger.info(person.getName());
                    logger.info(person.getName());
                }
            }
        }.start();

        new Thread(){
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    RPCBuilder rpcBuilder = RPCFactory.getInstance().getRpcBuilder();
                    Person person = rpcBuilder.getProxy(Person.class, 1, "127.0.0.1", 9090, 100);
                    person.setAge(26);
                    logger.info(person.getAge());
                    logger.info(person.getAge());
                    logger.info(person.getName());
                }
            }
        }.start();


    }

}
