package com.scalahome.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by xufuqing on 16/5/16.
 */
public class DefaultRPCBuilder implements RPCBuilder {

    private Logger logger = LoggerFactory.getLogger(DefaultRPCBuilder.class);

    @Override
    public <T> T getProxy(Class<T> clazz, long versionID, String host, int port, long timeout) throws InterruptedException {
        return CglibMethodInject.getInstance().getInstance(clazz, new DefaultMethodInterceptor(versionID, host, port, timeout));
    }

    private Serializer serializer = new DecoratedProtoStuffSerializer();

    @Override
    public <T> void startServer(final T t, final long versionID, String host, int port) throws InterruptedException {
        TCPServer tcpServer = new NettyServer();
        tcpServer.setOnReceiveListener(new TCPServer.OnReceiveListener() {
            @Override
            public byte[] onReceive(byte[] data) {
                logger.info("data len:" + data.length);
                long clientVersionId = IOUtils.byteArrayToLong(data, 0);
                if (clientVersionId != versionID) {
                    logger.error("Version Diff, Server:" + versionID + ", Client:" + clientVersionId);
                    return null;
                }
                byte methodNameLength = data[8];
                byte paramsNum = data[9];
                int clientParamsTypeHashCode = IOUtils.byteArrayToInt(data, 10);
                String methodName = null;
                try {
                    methodName = new String(data, 14, methodNameLength, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    logger.error(IOUtils.getStackTrace(e));
                }
                Method[] methods = t.getClass().getMethods();
                for (Method method : methods) {
                    if (methodName.equals(method.getName()) && paramsNum == method.getParameterCount()) {
                        Class<?>[] parameterTypes = method.getParameterTypes();
                        StringBuffer buffer = new StringBuffer();
                        for (Class<?> parameterType : parameterTypes) {
                            buffer.append(parameterType.getCanonicalName());
                        }
                        int paramsTypeHashCode = buffer.toString().hashCode();
                        if (paramsTypeHashCode == clientParamsTypeHashCode) {
                            Object[] params = new Object[paramsNum];
                            for (int i = 0; i < params.length; i++) {
                                byte paramLength = data[14 + methodNameLength + i];
                                int offset = 14 + methodNameLength + paramsNum;
                                for(int j = 0; j < i; j++) {
                                    offset += data[14 + methodNameLength + j];
                                }
                                byte[] paramData = Arrays.copyOfRange(data, offset, offset + paramLength);
                                try {
                                    params[i] = serializer.deSerialize(parameterTypes[i], paramData);
                                } catch (ReflectiveOperationException e) {
                                    logger.error(IOUtils.getStackTrace(e));
                                    return null;
                                }
                            }
                            try {
                                Object result = method.invoke(t, params);
                                Class returnType = method.getReturnType();
                                if (returnType == void.class) {
                                    return new byte[0];
                                } else {
                                    if (result == null) {
                                        return new byte[0];
                                    } else {
                                        return serializer.serialize(returnType, result);
                                    }
                                }
                            } catch (Exception e) {
                                logger.error(IOUtils.getStackTrace(e));
                            }
                        }
                    }
                }
                return null;
            }
        });
        tcpServer.start(host, port);
    }
}
