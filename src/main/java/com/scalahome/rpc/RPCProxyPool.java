package com.scalahome.rpc;


import com.scalahome.common.SnowFlake;
import com.scalahome.common.closablepool.CloseablePool;
import com.scalahome.common.serialize.CommonSerializer;
import com.scalahome.common.serialize.CommonSerializerFactory;
import com.scalahome.net.Client;
import com.scalahome.net.ClientPool;
import com.scalahome.net.OnReadListener;
import com.scalahome.net.Server;
import com.scalahome.rpc.erros.RPCException;
import com.scalahome.rpc.erros.RemoteException;
import com.scalahome.rpc.erros.TimeoutException;
import com.scalahome.rpc.proto.Request;
import com.scalahome.rpc.proto.Response;
import io.netty.channel.ChannelHandlerContext;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.commons.lang3.StringUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author fuqing.xu
 * @date 2020-07-11 16:42
 */
@Slf4j
public class RPCProxyPool extends CloseablePool {

    private final ClientPool clientPool;
    private final Server server;
    private final SnowFlake snowFlake;
    private final long timeout;
    private final Map<InetSocketAddress, Set<String>> resourceMap = new ConcurrentHashMap<>();

    public RPCProxyPool(long duration, TimeUnit unit, Server server, SnowFlake snowFlake, long timeout) {
        super(duration, unit);
        this.clientPool = new ClientPool(duration * 2, unit);
        this.server = server;
        this.snowFlake = snowFlake;
        this.timeout = timeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.clientPool.setConnectTimeout(connectTimeout);
    }

    public void closeProxy(Class clazz, InetSocketAddress remoteAddress) {
        closeProxy(clazz, clazz.getName(), remoteAddress);
    }

    public void closeProxy(Class clazz, String serviceName, InetSocketAddress remoteAddress) {
        resourceMap.compute(remoteAddress, (key, value) -> {
            if (value == null) {
                return null;
            }
            value.remove(clazz.getName() + "_" + serviceName);
            if (value.isEmpty()) {
                clientPool.closeClient(remoteAddress);
                return null;
            }
            return value;
        });
        super.closeResource(clazz.getName() + "_" + clazz.getName() + "_" + remoteAddress.toString());
    }

    public <T> T getProxy(Class<T> clazz, InetSocketAddress remoteAddress) throws ExecutionException, RPCException {
        return getProxy(clazz, clazz.getName(), remoteAddress, CommonSerializerFactory.newCommonSerializer(), this.timeout);
    }

    public <T> T getProxy(@NonNull Class<T> clazz, @NonNull String serviceName,
                          @NonNull InetSocketAddress remoteAddress,
                          @NonNull CommonSerializer rpcSerializer, long timeout)
            throws ExecutionException, RPCException {
        if (Arrays.stream(clazz.getMethods()).anyMatch(item -> !item.getReturnType().equals(void.class) && item.getAnnotation(Async.class) != null)) {
            throw new RPCException("Async Method Should Not Have A Return Value! Remove Async Or Change Return Type To void");
        }
        if (Arrays.stream(clazz.getMethods()).anyMatch(item -> item.getAnnotation(Timeout.class) != null && item.getAnnotation(Async.class) != null)) {
            throw new RPCException("Async Method Should Not Have A Timeout Value!");
        }
        // try local obj
        if (server != null && server.getLocalAddress() != null
                && server.getLocalAddress().equals(remoteAddress)
                && server.getService(serviceName) != null) {
            return (T) server.getService(serviceName);
        }
        // build remote proxy, its lazy, connect open until data send happened
        T proxy = super.getResource(clazz.getName() + "_" + serviceName + "_" + remoteAddress.toString(), new Callable<T>() {
            @Override
            public T call() throws Exception {
                MethodInterceptor methodInterceptor = new MethodInterceptor() {

                    private final Map<Long, WeakReference<Response>> responseMap = new ConcurrentHashMap<>();

                    private final OnReadListener onReadListener = new OnReadListener() {

                        private final byte[] empty = new byte[0];

                        @Override
                        public void onChannelInactive() {
                            try {
                                Map<Long, WeakReference<Response>> mapToNotify = new HashMap<>(responseMap);
                                mapToNotify.forEach((key, value) -> {
                                    WeakReference<Response> weakReference = responseMap.remove(key);
                                    Response response;
                                    if (weakReference != null && (response = weakReference.get()) != null) {
                                        response.setVersion(Constant.VERSION);
                                        response.setRequestId(key);
                                        response.setErrMsg("ChannelInactive");
                                        synchronized (response) {
                                            response.notify();
                                        }
                                    }
                                });
                            } catch (ConcurrentModificationException e) {
                                log.error(e.getMessage(), e);
                            }
                        }

                        @Override
                        public byte[] onRead(byte[] msg, ChannelHandlerContext ctx) {
                            try {
                                Response result = rpcSerializer.deSerialize(Response.class, msg, 0, msg.length);
                                WeakReference<Response> weakReference = responseMap.remove(result.getRequestId());
                                Response response;
                                if (weakReference != null && (response = weakReference.get()) != null) {
                                    response.setVersion(result.getVersion());
                                    response.setResult(result.getResult());
                                    response.setRequestId(result.getRequestId());
                                    response.setErrMsg(result.getErrMsg());
                                    synchronized (response) {
                                        response.notify();
                                    }
                                    return empty;
                                } else {
                                    // do nothing
                                }
                            } catch (Exception e) {
                                log.error(e.getMessage(), e);
                            }
                            return null;
                        }
                    };

                    private Client preClient;

                    @Override
                    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                        Request request = new Request(Constant.VERSION, serviceName, method.getName(), method.getParameterTypes(), args, snowFlake.nextId());
                        Client client = clientPool.getClient(remoteAddress);
                        if (preClient != client) {
                            preClient = client;
                            preClient.addOnReadListener(onReadListener);
                        }
                        Timeout methodTimeout = method.getAnnotation(Timeout.class);
                        Async async = method.getAnnotation(Async.class);
                        Response response = new Response();
                        try {
                            synchronized (response) {
                                responseMap.put(request.getRequestId(), new WeakReference<>(response));
                                client.sendMsg(rpcSerializer.serialize(Request.class, request));
                                if (async != null) {
                                    return null;
                                }
                                response.wait(methodTimeout != null ? methodTimeout.value() : timeout);
                            }
                        } finally {
                            responseMap.remove(request.getRequestId());
                        }
                        if (!StringUtils.isEmpty(response.getErrMsg())) {
                            throw new RemoteException(response.getErrMsg());
                        } else if (response.getRequestId() != null) {
                            return response.getResult();
                        } else {
                            throw new TimeoutException("Timeout");
                        }
                    }
                };
                return (T) Enhancer.create(clazz, methodInterceptor);
            }
        });
        resourceMap.compute(remoteAddress, (key, value) -> {
            if (value == null) {
                value = new HashSet<>();
            }
            value.add(clazz.getName() + "_" + serviceName);
            return value;
        });
        return proxy;
    }
}
