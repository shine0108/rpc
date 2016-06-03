package com.scalahome.rpc.tuple;

/**
 * Created by xufuqing on 16/5/30.
 */
public class Tuple_2<T, U> {
    public T first;
    public U second;

    public Tuple_2(T t, U u) {
        this.first = t;
        this.second = u;
    }
}
