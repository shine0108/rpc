package com.scalahome.rpc.proto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fuqing.xu
 * @date 2020-07-08 15:55
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Request {
    private int version;
    private String serviceName;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] args;
    private long requestId;
}
