package com.scalahome.rpc.demo;

import com.scalahome.rpc.RPCBuilder;
import com.scalahome.rpc.RPCFactory;
import com.scalahome.rpc.Server;
import org.apache.log4j.Logger;

/**
 * Created by xufuqing on 16/5/16.
 */
public class App {

    private static Logger logger = Logger.getLogger(App.class);

    public static void main(String[] args) throws InterruptedException {
        RPCBuilder rpcBuilder = RPCFactory.getInstance().getRpcBuilder();
        Server server = rpcBuilder.startServer(new PersonImpl(), "127.0.0.1", 9090);
        Person person = rpcBuilder.getProxy(Person.class, 1, "127.0.0.1", 9090, 1000);
        person.setName("jack");
        System.out.println(person.getName());
//        server.shutdown();
    }

}
