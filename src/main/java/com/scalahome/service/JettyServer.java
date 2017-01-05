package com.scalahome.service;

import com.scalahome.tuple.Tuple_2;
import com.scalahome.utils.IOUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fuqing.xfq on 2016/12/28.
 */
public class JettyServer implements Server {

    private Logger logger = Logger.getLogger(JettyServer.class);

    Map<String, Tuple_2<Object, Method>> serviceMap = new HashMap<String, Tuple_2<Object, Method>>();

    @Override
    public void start(String host, int port) throws Exception {
        final org.eclipse.jetty.server.Server server = new org.eclipse.jetty.server.Server(new InetSocketAddress(host, port));
        server.setHandler(new AbstractHandler() {
            @Override
            public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
                    throws IOException, ServletException {
                logger.info("handle request, path:" + s + ", " + request.getParameterMap());
                s = s.endsWith("/") ? s.substring(0, s.lastIndexOf("/")) : s;
                Tuple_2<Object, Method> service = serviceMap.get(s.toLowerCase());
                if(service != null) {
                    httpServletResponse.setContentType("text/html;charset=utf-8");
                    request.setHandled(true);
                    try {
                        Object result;
                        if(service.second.getParameterTypes().length > 0) {
                            Object[] args = new Object[service.second.getParameterTypes().length];
                            Params params = service.second.getAnnotation(Params.class);
                            String[] paramNames = params.value();
                            Map<java.lang.String,java.lang.String[]> parameterMap = request.getParameterMap();
                            for(int i = 0; i < paramNames.length; i++) {
                                String[] value = parameterMap.get(paramNames[i]);
                                if(value != null && value.length == 1) {
                                    args[i] = value[0];
                                }
                            }
                            result = service.second.invoke(service.first, args);
                        } else {
                            result = service.second.invoke(service.first);
                        }
                        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
                        if(service.second.getReturnType() == String.class) {
                            httpServletResponse.getWriter().write(result == null ? "" : result.toString());
                            httpServletResponse.getWriter().flush();
                            httpServletResponse.getWriter().close();
                        }
                    } catch (Exception e) {
                        String errMsg = IOUtils.getStackTrace(e);
                        logger.error(errMsg);
                        httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        httpServletResponse.getWriter().write(errMsg);
                        httpServletResponse.getWriter().flush();
                        httpServletResponse.getWriter().close();
                    }
                } else {
                    request.setHandled(false);
                }
            }
        });
        server.start();
//        server.join();
    }

    @Override
    public <T> void addService(Class<T> clazz, T t) {
        Path clazzPath = (Path) clazz.getAnnotation(Path.class);
        String parentPath = clazzPath == null ? clazz.getSimpleName().toLowerCase() : clazzPath.value().toLowerCase();
        Method[] methods = clazz.getMethods();
        for(Method method : methods) {
            if(method.getReturnType() != String.class && method.getReturnType() != void.class) {
                throw new RuntimeException("ReturnType Must Be String Or void!");
            }
            if(method.getParameterTypes().length > 0) {
                Params params = method.getAnnotation(Params.class);
                if(params == null || params.value().length != method.getParameterTypes().length) {
                    throw new RuntimeException("@Params Annotation Error!");
                }
                Class<?>[] paramTypes = method.getParameterTypes();
                for(Class paramType : paramTypes) {
                    if(paramType != String.class)
                        throw new RuntimeException("Param Type Must Be String!");
                }
            }
            Path methodPath = method.getAnnotation(Path.class);
            String childPath = methodPath == null ? method.getName().toLowerCase() : methodPath.value().toLowerCase();
            String path = (parentPath.startsWith("/") ? "" : "/") + parentPath + "/" + childPath;
            serviceMap.put(path, new Tuple_2<Object, Method>(t, method));
        }
    }
}
