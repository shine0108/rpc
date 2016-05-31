package com.scalahome.rpc.demo;

import com.scalahome.rpc.Async;

/**
 * Created by xufuqing on 16/5/24.
 */
public interface Node {
    NodeMsg getLeader();


    @Async
    void heartBeat(long versionID, String host, int port);

    class NodeMsg {
        long versionID;
        String host;
        int port;
        int workerNum;
    }

    class Request {

    }

    class Response {

    }
}