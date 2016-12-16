package com.scalahome.rpc.demo;

import com.scalahome.rpc.RPCBuilder;
import com.scalahome.rpc.RPCFactory;
import com.scalahome.rpc.Server;
import org.apache.log4j.Logger;

import java.util.BitSet;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by xufuqing on 16/5/16.
 */
public class App {

    private static Logger logger = Logger.getLogger(App.class);

    public static void main(String[] args) throws InterruptedException {

        final RPCBuilder rpcBuilder = RPCFactory.getInstance().getRpcBuilder();
        Server server = rpcBuilder.startServer(new PersonImpl(), "127.0.0.1", 9090);
        Thread.sleep(100);
        for(int i = 0; i < 5; i++) {
            new Thread(){
                @Override
                public void run() {
                    Person person = rpcBuilder.getProxy(Person.class, 1, "127.0.0.1", 9090, 100);
                    for(int i = 0; i < 10000; i++) {
                        try {
                            person.setName("tom_" + i);
                            increase();
                            System.out.println(person.getName());
                            increase();
                            person.setAge(i);
                            increase();
                            System.out.println(person.getAge());
                            increase();
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                            break;
                        }

//                    System.out.println(person.getName());
                    }
                    logger.info("over");
                }
            }.start();

            new Thread(){
                @Override
                public void run() {
                    Person person = rpcBuilder.getProxy(Person.class, 1, "127.0.0.1", 9090, 100);
                    for(int i = 0; i < 1000; i++) {
                        try {
                            person.setAge(i);
                            increase();
                            System.out.println(person.getAge());
                            increase();
                            person.setName("tom_" + i);
                            increase();
                            System.out.println(person.getName());
                            increase();
                        } catch (Exception e) {
                            e.printStackTrace();
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                            break;
                        }

//                    System.out.println(person.getName());
                    }
                    logger.info("over");
                }
            }.start();
        }

    }

    public static AtomicLong count = new AtomicLong();

    public static void increase() {
        long value = count.incrementAndGet();
        if(value % 1000 == 0) {
            logger.info("===================================== current_num:" + value);
        }
    }
}
