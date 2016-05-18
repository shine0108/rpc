package com.scalahome.rpc.demo;

import com.scalahome.rpc.Async;

import java.util.List;

/**
 * Created by xufuqing on 16/5/18.
 */
public interface Person {
    @Async
    void setAge(int age, int value);

    String getName();

    void sendData(byte[] data);

    void sendObj(Object obj);

    void sendInt(int[] ints);

    void sendBean(Bean bean);

    void sendStr(String[] args);

    void sendList(List<String> list);

    class Bean {
        int year;
        Object any;

        @Override
        public String toString() {
            return year + "_" + any;
        }
    }
}
