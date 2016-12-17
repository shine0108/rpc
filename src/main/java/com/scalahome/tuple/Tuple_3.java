package com.scalahome.tuple;

/**
 * Created by xufuqing on 16/5/30.
 */
public class Tuple_3<T, U, V> {
    public T first;
    public U second;
    public V third;

    public Tuple_3(T t, U u, V v) {
        this.first = t;
        this.second = u;
        this.third = v;
    }
}
