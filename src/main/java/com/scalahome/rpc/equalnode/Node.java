package com.scalahome.rpc.equalnode;

import com.scalahome.rpc.VersionedProtocol;

/**
 * Created by xufuqing on 16/5/31.
 */
public interface Node extends VersionedProtocol{
    NodeMsg getLeader();
    int getFollowerNum();
    int heartBeat(NodeMsg nodeMsg);
    boolean electLeader(NodeMsg nodeMsg);
    boolean confirmLeader(NodeMsg nodeMsg, int followerNum);
}
