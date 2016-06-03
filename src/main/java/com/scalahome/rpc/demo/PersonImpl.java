package com.scalahome.rpc.demo;

import org.apache.log4j.Logger;

/**
 * Created by xufuqing on 16/5/31.
 */
public class PersonImpl implements Person {

    private Logger logger = Logger.getLogger(PersonImpl.class);
    private String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        logger.info("name:" + name);
        this.name = name;
    }

    @Override
    public long getVersionID() {
        return 1L;
    }
}
