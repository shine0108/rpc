package com.scalahome.test;

import com.scalahome.net.Server;
import com.scalahome.rpc.RPCEndpointBuilder;
import com.scalahome.test.IRPCTest;
import com.scalahome.test.IRPCTestImpl;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author fuqing.xu
 * @date 2020-07-14 14:12
 */
@Slf4j
public class RPCServerTest {
    public static void main(String[] args) throws IOException {
        InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", 9090);
        Server server = RPCEndpointBuilder.startEndpoint(inetSocketAddress);
        log.info("endpoint start");
        server.addService(IRPCTest.class.getName(), new IRPCTestImpl());
    }
}
