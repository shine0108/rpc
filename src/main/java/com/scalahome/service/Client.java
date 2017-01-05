package com.scalahome.service;

import java.util.Map;

/**
 * Created by fuqing.xfq on 2016/12/28.
 */
public interface Client {
    void setTimeOut(int timeOut);
    String request(String host, int port, String path, Map<String, String> params) throws Exception;
}
