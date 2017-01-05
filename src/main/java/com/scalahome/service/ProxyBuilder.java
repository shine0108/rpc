package com.scalahome.service;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fuqing.xfq on 2017/1/5.
 */
public class ProxyBuilder {
    private static ProxyBuilder instance = new ProxyBuilder();

    public static ProxyBuilder getInstance() {
        return instance;
    }

    public <T> T getProxy(Class<T> clazz, final String host, final int port, int timeOut) {
        final DefaultHttpClient client = new DefaultHttpClient();
        client.setTimeOut(timeOut);
        Path clazzPath = clazz.getAnnotation(Path.class);
        final String parentPath = clazzPath == null ? clazz.getSimpleName().toLowerCase() : clazzPath.value();
        return (T)Enhancer.create(clazz, new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                Path methodPath = method.getAnnotation(Path.class);
                String childPath = methodPath == null ? method.getName().toLowerCase() : methodPath.value().toLowerCase();
                String path = (parentPath.startsWith("/") ? "" : "/") + parentPath + "/" + childPath;
                Map<String, String> params = new HashMap<String, String>();
                if(method.getParameterTypes().length > 0) {
                    Params paramNames = method.getAnnotation(Params.class);
                    if(paramNames == null || paramNames.value().length != method.getParameterTypes().length) {
                        throw new RuntimeException("@Params Annotation Error!");
                    }
                    Class<?>[] paramTypes = method.getParameterTypes();
                    for(Class paramType : paramTypes) {
                        if(paramType != String.class)
                            throw new RuntimeException("Param Type Must Be String!");
                    }
                    for(int i = 0; i < paramNames.value().length; i++) {
                        params.put(paramNames.value()[i], (String) objects[i]);
                    }
                }
                return client.request(host, port, path, params);
            }
        });
    }
}
