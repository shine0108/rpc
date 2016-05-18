package com.scalahome.rpc;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

/**
 * Created by xufuqing on 16/5/17.
 */
public class CglibMethodInject {

    private static CglibMethodInject instance = new CglibMethodInject();

    public static CglibMethodInject getInstance() {
        return instance;
    }

    public <T> T getInstance(Class<T> clazz, MethodInterceptor methodInterceptor) {
        return (T) Enhancer.create(clazz, methodInterceptor);
    }
}
