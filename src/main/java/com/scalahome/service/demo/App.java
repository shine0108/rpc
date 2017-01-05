package com.scalahome.service.demo;

import com.scalahome.rpc.demo.*;
import com.scalahome.service.JettyServer;
import com.scalahome.service.ProxyBuilder;
import com.scalahome.service.Server;

/**
 * Created by fuqing.xfq on 2016/12/28.
 */
public class App {
    public static void main(String[] args) throws Exception {
//        System.out.println(App.class.getName());
//        System.out.println(App.class.getSimpleName());
//        String path = "/test/abc";
//        System.out.println(path.split("/")[0]);
//        System.out.println(path.split("/")[1]);
//        System.out.println(path.split("/")[2]);
//        System.out.println(path.split("/").length);
//        System.out.println(App.class.getMethods()[1].getParameters()[1].getName());


//        test(new String[]{"a", "b"});
//        Object obj = null;
//        System.out.println(obj.toString());

        Server server = new JettyServer();
        server.addService(Person.class, new PersonImpl());
        server.start("127.0.0.1", 9090);

        Person person = ProxyBuilder.getInstance().getProxy(Person.class, "127.0.0.1", 9090, 10 * 1000);
        person.setName("jack");
        System.out.println("getName:" + person.getName());
    }

    public static void test(Object ... args) {
        for(int i = 0; i < args.length; i++) {
            System.out.println(args[i]);
        }
    }
}
