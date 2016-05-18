package com.scalahome.rpc.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xufuqing on 16/5/18.
 */
public class PersonImpl implements Person {

    private Logger logger = LoggerFactory.getLogger(PersonImpl.class);

    @Override
    public void setAge(int age, int value) {
     logger.info("setAge:" + age + "," + value);
    }

    @Override
    public String getName() {
        logger.info("get name");
        return "jack";
    }
}
