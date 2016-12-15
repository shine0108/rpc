package com.scalahome.rpc.demo;

import com.scalahome.rpc.RPCBuilder;
import com.scalahome.rpc.RPCFactory;
import com.scalahome.rpc.Server;
import org.apache.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by xufuqing on 16/5/16.
 */
public class App {

    private static Logger logger = Logger.getLogger(App.class);

    public static void main(String[] args) throws InterruptedException {
        new Thread(){
            @Override
            public void run() {
                RPCBuilder rpcBuilder = RPCFactory.getInstance().getRpcBuilder();
                try {
                    Server server = rpcBuilder.startServer(new PersonImpl(), "127.0.0.1", 9090);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        Thread.sleep(10);
        for(int i = 0; i < 10; i++) {
            new Thread(){
                @Override
                public void run() {
                    RPCBuilder rpcBuilder = RPCFactory.getInstance().getRpcBuilder();
                    Person person = rpcBuilder.getProxy(Person.class, 1, "127.0.0.1", 9090, 100);
                    for(int i = 0; i < 10000; i++) {
                        person.setName("jack" + i);
                        System.out.println(person.getName());
                    }
                    System.out.println("finished");
                }
            }.start();
            new Thread(){
                @Override
                public void run() {
                    RPCBuilder rpcBuilder = RPCFactory.getInstance().getRpcBuilder();
                    Person person = rpcBuilder.getProxy(Person.class, 1, "127.0.0.1", 9090, 100);
                    for(int i = 0; i < 10000; i++) {
                        person.setName("tom" + i);
                        System.out.println(person.getName());
                    }
                    System.out.println("finished");
                }
            }.start();
        }


    }

}
