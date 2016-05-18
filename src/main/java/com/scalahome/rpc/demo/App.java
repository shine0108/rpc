package com.scalahome.rpc.demo;

import com.scalahome.rpc.DefaultRPCBuilder;
import com.scalahome.rpc.RPCBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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
        person.sendData("hello".getBytes());
        Person.Bean bean = new Person.Bean();
        bean.any = "yes";
        person.sendBean(bean);
        List<String> l = new ArrayList<String>();
        l.add("a");
        person.sendList(l);
        person.sendStr(new String[]{"a","b"});
        person.sendInt(new int[]{2,3,4});
        person.sendObj("yes here");
    }


}
