package com.scalahome.service.demo;

/**
 * Created by fuqing.xfq on 2017/1/4.
 */
public class PersonImpl implements Person {
    private String name;

    @Override
    public void setName(String name) {
        this.name = name;
        System.out.println("set name:" + name);
    }

    @Override
    public String getName() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("get name:" + this.name);
        return name;
    }
}
