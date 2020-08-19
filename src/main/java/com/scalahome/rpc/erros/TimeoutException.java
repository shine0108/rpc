package com.scalahome.rpc.erros;

/**
 * @author fuqing.xu
 * @date 2020-07-13 20:21
 */
public class TimeoutException extends RPCException {
    public TimeoutException() {
    }

    public TimeoutException(Exception e) {
        super(e);
    }

    public TimeoutException(String msg) {
        super(msg);
    }

    public TimeoutException(String msg, Exception e) {
        super(msg, e);
    }
}
