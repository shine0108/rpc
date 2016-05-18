package com.scalahome.rpc.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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

    @Override
    public void sendData(byte[] data) {
        logger.info(new String(data));
    }

    @Override
    public void sendObj(Object obj) {
        logger.info("obj:" + obj.toString());
    }

    @Override
    public void sendInt(int[] ints) {
        logger.info("ints:" + ints.length);
    }

    @Override
    public void sendBean(Bean bean) {
        logger.info("bean:" + bean.toString());
    }

    @Override
    public void sendStr(String[] args) {
        logger.info("args:" + args.length);
    }

    @Override
    public void sendList(List<String> list) {
        logger.info(list.toString());
    }
}
