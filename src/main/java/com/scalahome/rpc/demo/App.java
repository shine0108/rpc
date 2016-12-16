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
        final RPCBuilder rpcBuilder = RPCFactory.getInstance().getRpcBuilder();
        Server server = rpcBuilder.startServer(new PersonImpl(), "127.0.0.1", 9090);
        Thread.sleep(1000);
        for(int i = 0; i < 10; i++) {
            new Thread(){
                @Override
                public void run() {
                    Person person = rpcBuilder.getProxy(Person.class, 1, "127.0.0.1", 9090, 300);
                    for(int i = 0; i < 10000; i++) {
                        try {
                            person.setName("tom_" + i);
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

}
