package com.scalahome.rpc.demo;

import org.apache.log4j.Logger;

import java.util.Map;

/**
 * Created by xufuqing on 16/5/31.
 */
public class PersonImpl implements Person {

    private Logger logger = Logger.getLogger(PersonImpl.class);
    private String name;
    private int age;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
        logger.info("name:" + name);
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setAge(int age) {
        this.age = age;
        logger.info("age:" + age);
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getAge() {
        return age;
    }

    @Override
    public void setAttribute(Map<String, String> msg) {
        logger.info(msg);
    }

    @Override
    public long getVersionID() {
        return 1L;
    }
}
