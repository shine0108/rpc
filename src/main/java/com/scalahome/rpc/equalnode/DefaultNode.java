package com.scalahome.rpc.equalnode;

import com.scalahome.rpc.DecoratedHashMap;
import com.scalahome.rpc.DefaultRPCBuilder;
import com.scalahome.rpc.IOUtils;
import com.scalahome.rpc.RPCBuilder;
import com.scalahome.rpc.tuple.Tuple_2;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by xufuqing on 16/5/31.
 */
public class DefaultNode implements Node {

    private final long TIME_OUT = 100;

    private final NodeMsg nodeMsg;

    private Logger logger = Logger.getLogger(DefaultNode.class);

    private RPCBuilder rpcBuilder = new DefaultRPCBuilder();

    public DefaultNode(NodeMsg nodeMsg) {
        this(nodeMsg, null);
    }

    public DefaultNode(NodeMsg nodeMsg, List<NodeMsg> neighbourList) {
        this.nodeMsg = nodeMsg;
        if (neighbourList != null) {
            for (NodeMsg neighbour : neighbourList) {
                nodes.put(neighbour.getKey(), neighbour);
            }
        }
        init();
    }

    private void init() {
        for (NodeMsg node : nodes.values()) {
            try {
                Node proxy = rpcBuilder.getProxy(Node.class, getVersionID(), node.host, node.port, TIME_OUT);
                NodeMsg leader = proxy.getLeader();
                if (leader != null) {
                    Node leaderProxy = rpcBuilder.getProxy(Node.class, getVersionID(), leader.host, leader.port, TIME_OUT);
                    int followerNum = leaderProxy.getFollowerNum();
                    if (followerNum > 0) {
                        this.leader = new Tuple_2<NodeMsg, Integer>(leader, followerNum);
                        role = Role.FOLLOWER;
                        break;
                    }
                }
            } catch (Exception e) {
                logger.error(IOUtils.getStackTrace(e));
            }
        }
        if (role == Role.UNKNOWN) {
            boolean electSuccess = electLeader(nodeMsg);
            if (electSuccess) {
                leader = new Tuple_2<NodeMsg, Integer>(this.nodeMsg, activeFollower.size());
                role = Role.LEADER;
                for (NodeMsg node : nodes.values()) {
                    try {
                        Node proxy = rpcBuilder.getProxy(Node.class, getVersionID(), node.host, node.port, TIME_OUT);
                        if (!proxy.electLeader(this.nodeMsg) || !proxy.confirmLeader(this.nodeMsg, activeFollower.size())) {
                            NodeMsg leader = proxy.getLeader();
                            if (leader != null) {
                                Node leaderProxy = rpcBuilder.getProxy(Node.class, getVersionID(), leader.host, leader.port, TIME_OUT);
                                int followerNum = leaderProxy.getFollowerNum();
                                if (followerNum > activeFollower.size()) {
                                    this.leader = new Tuple_2<NodeMsg, Integer>(leader, followerNum);
                                    role = Role.FOLLOWER;
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.error(IOUtils.getStackTrace(e));
                    }
                }
            } else {
                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e) {
                    logger.error(IOUtils.getStackTrace(e));
                }
            }
        }
        if (role == Role.UNKNOWN) {
            logger.fatal("Illegal Role");
            System.exit(1);
        }
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (role == Role.FOLLOWER && leader != null) {
                        if (leaderProxy == null) {
                            leaderProxy = rpcBuilder.getProxy(Node.class, getVersionID(), leader.first.host, leader.first.port, TIME_OUT);
                        }
                        int followerNum = leaderProxy.heartBeat(DefaultNode.this.nodeMsg);
                        if(followerNum == -1) {
                            NodeMsg newLeader = leaderProxy.getLeader();
                            if(newLeader != null) {
                                //TODO
                            }
                        }
                    }
                } catch (Exception e) {
                    IOUtils.getStackTrace(e);
                }
            }
        }, 1000, 1000);
    }

    private Tuple_2<NodeMsg, Integer> leader;

    private Node leaderProxy;

    private Role role = Role.UNKNOWN;

    private AtomicBoolean elected = new AtomicBoolean(false);

    private Map<String, NodeMsg> nodes = new DecoratedHashMap<String, NodeMsg>();

    private Map<String, NodeMsg> activeFollower = new DecoratedHashMap<String, NodeMsg>();

    @Override
    public NodeMsg getLeader() {
        return leader == null ? null : leader.first;
    }

    @Override
    public int getFollowerNum() {
        return leader == null ? 0 : leader.second;
    }

    @Override
    public int heartBeat(NodeMsg nodeMsg) {
        if (role == Role.LEADER) {
            return activeFollower.size();
        } else {
            return -1;
        }
    }

    @Override
    public boolean electLeader(NodeMsg nodeMsg) {
        if (leader == null && !elected.get()) {
            return elected.compareAndSet(false, true);
        }
        return false;
    }

    @Override
    public boolean confirmLeader(NodeMsg nodeMsg, int followerNum) {
        if (leader == null || leader.second < followerNum) {
            leader = new Tuple_2<NodeMsg, Integer>(nodeMsg, followerNum);
            leaderProxy = rpcBuilder.getProxy(Node.class, getVersionID(), leader.first.host, leader.first.port, TIME_OUT);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public long getVersionID() {
        return 1L;
    }
}
