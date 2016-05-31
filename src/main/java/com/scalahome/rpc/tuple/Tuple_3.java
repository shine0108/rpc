package com.scalahome.rpc.tuple;

/**
 * Created by xufuqing on 16/5/30.
 */
public class Tuple_3<T, U, V> {
    public final T first;
    public final U second;
    public final V third;

    Tuple_3(T t, U u, V v) {
        this.first = t;
        this.second = u;
        this.third = v;
    }
}
