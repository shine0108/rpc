package com.scalahome.rpc.demo;

import com.scalahome.rpc.Async;

/**
 * Created by xufuqing on 16/5/18.
 */
public interface Person {
    @Async
    void setAge(int age, int value);

    void setAge(int age, String value);

    String getName();

    void sendData(byte[] data);

    void sendBean(Bean bean);


    class Bean {
        int year;
        Object any;

        @Override
        public String toString() {
            return year + "_" + any;
        }
    }
}
