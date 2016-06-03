package com.scalahome.rpc.equalnode;

/**
 * Created by xufuqing on 16/5/31.
 */
public class NodeMsg implements Comparable<NodeMsg> {
    public String host;
    public int port;

    public NodeMsg(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public int hashCode() {
        return getKey().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NodeMsg) {
            return port == ((NodeMsg) obj).port && host.equals(((NodeMsg) obj).host);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return getKey();
    }

    public String getKey() {
        return host + ":" + port;
    }

    @Override
    public int compareTo(NodeMsg o) {
        return getKey().compareTo(o.getKey());
    }
}
