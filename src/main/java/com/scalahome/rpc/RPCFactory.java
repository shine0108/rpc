package com.scalahome.rpc;
import com.scalahome.rpc.serialize.ExtendedProtoStuffSerializer;
import com.scalahome.rpc.serialize.RPCSerializer;

/**
 * Created by fuqing.xfq on 2016/12/7.
 */
public class RPCFactory {
    private RPCFactory() {}
    private static RPCFactory instance = new RPCFactory();

    public static RPCFactory getInstance() {
        return instance;
    }

    private RPCSerializer serializer = new ExtendedProtoStuffSerializer();

    public RPCSerializer getSerializer() {
        return serializer;
    }

    public Server getServer() {
        return new NettyServer();
    }

    public Client getClient() {
        return new NettyClient();
    }

    private RPCBuilder rpcBuilder = new RPCBuilderImpl();

    public RPCBuilder getRpcBuilder() {
        return rpcBuilder;
    }
}
