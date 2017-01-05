package com.scalahome.service;

/**
 * Created by fuqing.xfq on 2016/12/28.
 */
public interface Server {
    void start(String host, int port) throws Exception;
    <T> void addService(Class<T> clazz, T t);
}
