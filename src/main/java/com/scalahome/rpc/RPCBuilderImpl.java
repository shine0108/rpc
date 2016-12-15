package com.scalahome.rpc;

import com.scalahome.rpc.serialize.RPCSerializer;
import com.scalahome.rpc.utils.IOUtils;
import io.netty.channel.ChannelHandlerContext;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketAddress;
import java.util.concurrent.TimeoutException;

/**
 * Created by fuqing.xfq on 2016/12/7.
 */
public class RPCBuilderImpl implements RPCBuilder {

    private Logger logger = Logger.getLogger(RPCBuilderImpl.class);

    @Override
    public <T> T getProxy(Class<T> clazz, final long versionID, final String host, final int port, final long timeout) {
        MethodInterceptor interceptor = new MethodInterceptor() {

            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                returnMessage = null;
                Message msg = new Message();
                msg.versionId = versionID;
                msg.methodName = method.getName();
                msg.parameterTypes = method.getParameterTypes();
                msg.returnType = method.getReturnType();
                if(void.class == msg.returnType) {
                    msg.returnType = Void.class;
                }
                msg.args = objects;
                Sync sync = method.getAnnotation(Sync.class);
                msg.requestCode = sync == null ? 0 : 1;
                client.sendMsg(msg);
                if(sync != null) {
                    synchronized (lock) {
                        lock.wait(timeout);
                    }
                    if(returnMessage == null) {
                        throw new TimeoutException("TimeOut:" + timeout);
                    } else if(returnMessage.responseCode == 1) {
                        return returnMessage.returnValue;
                    } else {
                        throw new RemoteException("Response Error, Response Code:" + returnMessage.responseCode);
                    }
                }
                return null;
            }

            private Object lock = new Object();

            private Message returnMessage;

            final Client client = RPCFactory.getInstance().getClient();

            {
                client.setOnReceiveListener(new OnReceiveListener() {
                    @Override
                    public void onReceive(ChannelHandlerContext ctx, Message message) {
                        synchronized (lock) {
                            returnMessage = message;
                            lock.notifyAll();
                        }
                    }
                });
                try {
                    client.connect(host, port);
                } catch (InterruptedException e) {
                    logger.error(IOUtils.getStackTrace(e));
                    throw new RuntimeException(e);
                }
            }
        };
        T t = CglibMethodInject.getInstance().getInstance(clazz, interceptor);
        return t;
    }

    @Override
    public <T extends VersionedProtocol> Server startServer(final T t, String host, int port) throws InterruptedException {
        Server server = RPCFactory.getInstance().getServer();
        server.setOnReceiveListener(new OnReceiveListener() {
            @Override
            public void onReceive(ChannelHandlerContext ctx, Message message) {
                SocketAddress remoteAddress = ctx.channel().remoteAddress();
                long versionId = t.getVersionID();
                if(versionId != message.versionId) {
                    logger.warn("Version Conflict, server versionId:" + versionId + ", client versionId:" + message.versionId
                            + ", remove address:" + remoteAddress + ", request:" + message);
                    message.responseCode = -1;
                } else {
                    try {
                        Method method = t.getClass().getMethod(message.methodName, message.parameterTypes);
                        Object returnValue = method.invoke(t, message.args);
                        message.returnValue = returnValue;
                        message.responseCode = 1;
                    } catch (NoSuchMethodException e) {
                        logger.warn(IOUtils.getStackTrace(e));
                        message.responseCode = -2;
                    } catch (InvocationTargetException e) {
                        logger.warn(IOUtils.getStackTrace(e));
                        message.responseCode = -3;
                    } catch (IllegalAccessException e) {
                        logger.warn(IOUtils.getStackTrace(e));
                        message.responseCode = -4;
                    }
                }
                if(message.requestCode == 1) {
                    RPCSerializer serializer = RPCFactory.getInstance().getSerializer();
                    byte[] data = serializer.serialize(Message.class, message);
                    ctx.channel().writeAndFlush(data);
                }
            }
        });
        server.start(host, port);
        return server;
    }
}
