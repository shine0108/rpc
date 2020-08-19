package com.scalahome.rpc.erros;

public class RPCException extends Exception {
    public RPCException() {
    }

    public RPCException(Exception e) {
        super(e);
    }

    public RPCException(String msg) {
        super(msg);
    }

    public RPCException(String msg, Exception e) {
        super(msg, e);
    }
}
