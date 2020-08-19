package com.scalahome.rpc.erros;

/**
 * @author fuqing.xu
 * @date 2020-07-14 14:29
 */
public class RemoteException extends RPCException {
    public RemoteException() {
    }

    public RemoteException(Exception e) {
        super(e);
    }

    public RemoteException(String msg) {
        super(msg);
    }

    public RemoteException(String msg, Exception e) {
        super(msg, e);
    }
}
