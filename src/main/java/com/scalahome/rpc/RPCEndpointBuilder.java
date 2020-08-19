package com.scalahome.rpc;


import com.scalahome.common.ErrorUtils;
import com.scalahome.common.serialize.CommonSerializer;
import com.scalahome.common.serialize.CommonSerializerFactory;
import com.scalahome.net.OnReadListener;
import com.scalahome.net.Server;
import com.scalahome.net.ServerImpl;
import com.scalahome.rpc.erros.RPCException;
import com.scalahome.rpc.proto.Request;
import com.scalahome.rpc.proto.Response;
import io.netty.channel.ChannelHandlerContext;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;

/**
 * @author fuqing.xu
 * @date 2020-07-08 15:54
 */
@Slf4j
public class RPCEndpointBuilder {

    public static Server startEndpoint(InetSocketAddress localAddress) throws IOException {
        return startEndpoint(localAddress, CommonSerializerFactory.newCommonSerializer());
    }

    public static Server startEndpoint(@NonNull InetSocketAddress localAddress,
                                       @NonNull CommonSerializer rpcSerializer) throws IOException {
        Server server = new ServerImpl(localAddress);
        server.setOnReadListener(new OnReadListener() {
            @Override
            public byte[] onRead(byte[] msg, ChannelHandlerContext ctx) {
                try {
                    Request request = rpcSerializer.deSerialize(Request.class, msg, 0, msg.length);
                    String errMsg = null;
                    Object result = null;
                    Method method = null;
                    try {
                        if (request.getVersion() != Constant.VERSION) {
                            log.error("Version Error:" + request.getVersion() + "," + Constant.VERSION);
                            throw new RPCException("Version Error,expect:" + Constant.VERSION + ",actual:" + request.getVersion());
                        }
                        Object service = server.getService(request.getServiceName());
                        if (service == null) {
                            log.error("No Such Service:" + request.getServiceName());
                            throw new RPCException("No Such Service:" + request.getServiceName());
                        }
                        method = service.getClass().getMethod(request.getMethodName(), request.getParameterTypes());
                        result = method.invoke(service, request.getArgs());
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        errMsg = ErrorUtils.getStackTrace(e);
                    }
                    if (method != null && method.getAnnotation(Async.class) != null && method.getReturnType().equals(void.class)) {
                        return null;
                    }
                    Response response = new Response(request.getVersion(), result, request.getRequestId(), errMsg);
                    return rpcSerializer.serialize(Response.class, response);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    return null;
                }
            }
        });
        server.start();
        return server;
    }

}
