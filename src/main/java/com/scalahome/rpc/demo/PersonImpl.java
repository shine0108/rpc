package com.scalahome.rpc.demo;

import org.apache.log4j.Logger;

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
    }

    @Override
    public void setAge(int age) {
        this.age = age;
        logger.info("age:" + age);
    }

    @Override
    public int getAge() {
        return age;
    }

    @Override
    public long getVersionID() {
        return 1L;
    }
}
