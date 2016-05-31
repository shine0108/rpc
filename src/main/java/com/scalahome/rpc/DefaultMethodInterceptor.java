package com.scalahome.rpc;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Created by xufuqing on 16/5/17.
 */
public class DefaultMethodInterceptor implements MethodInterceptor {

    private final String host;
    private final int port;
    private Logger logger = LoggerFactory.getLogger(DefaultMethodInterceptor.class);

    private final long versionID;
    private final long timeout;

    private TCPClient tcpClient;

    public DefaultMethodInterceptor(long versionID, String host, int port, long timeout) {
        this.versionID = versionID;
        tcpClient = new NettyClient();
        this.timeout = timeout;
        this.host = host;
        this.port = port;
    }

    private Serializer serializer = new ExtendedProtoStuffSerializer();

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        byte[] version = IOUtils.longToByteArray(versionID);
        byte[] methodName = method.getName().getBytes("UTF-8");
        byte[][] params = new byte[objects.length][];
        Class[] parameterTypes = method.getParameterTypes();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < parameterTypes.length; i++) {
            params[i] = serializer.serialize(parameterTypes[i], objects[i]);
            buffer.append(parameterTypes[i].getCanonicalName());
        }
        byte[] paramsTypeHashCode = IOUtils.intToByteArray(buffer.toString().hashCode());
        int paramsLength = 0;
        for (byte[] param : params) {
            paramsLength += param.length;
        }
        byte[] data = new byte[8 + 1 + 1 + 4 + methodName.length + params.length + paramsLength];
        System.arraycopy(version, 0, data, 0, 8);
        data[8] = (byte) methodName.length;
        data[9] = (byte) params.length;
        System.arraycopy(paramsTypeHashCode, 0, data, 8 + 1 + 1, 4);
        System.arraycopy(methodName, 0, data, 8 + 1 + 1 + 4, methodName.length);
        for (int i = 0; i < params.length; i++) {
            data[8 + 1 + 1 + 4 + methodName.length + i] = (byte) params[i].length;
            int offset = 0;
            for (int j = 0; j < i; j++) {
                offset += params[j].length;
            }
            System.arraycopy(params[i], 0, data, 8 + 1 + 1 + 4 + methodName.length + params.length + offset, params[i].length);
        }
        tcpClient.connect(host, port);
        Async async = method.getAnnotation(Async.class);
        if (void.class == method.getReturnType() && async != null) {
            tcpClient.sendAsync(data);
            return null;
        } else {
            byte[] ack = tcpClient.sendSync(data, timeout);
            if (ack == null) {
                throw new RemoteException("TimeOut:" + timeout);
            } else {
                if (ack.length == 0) {
                    return null;
                } else {
                    return serializer.deSerialize(method.getReturnType(), ack);
                }
            }
        }
    }
}
