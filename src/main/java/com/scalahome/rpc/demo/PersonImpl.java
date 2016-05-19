package com.scalahome.rpc.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by xufuqing on 16/5/18.
 */
public class PersonImpl implements Person {

    private Logger logger = LoggerFactory.getLogger(PersonImpl.class);

    public void setAge(int age, int value) {
        logger.info("setAge:" + age + "," + value);
    }

    public void setAge(int age, String value) {
        logger.info("setAge2:" + age + "," + value);
    }

    public String getName() {
        logger.info("get name");
        return "jack";
    }

    public void sendData(byte[] data) {
        logger.info(new String(data));
    }

    public void sendBean(Person.Bean bean) {
        logger.info("bean:" + bean.toString());
    }

}
