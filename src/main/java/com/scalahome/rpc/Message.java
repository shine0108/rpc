package com.scalahome.rpc;

import com.alibaba.fastjson.JSON;

/**
 * Created by fuqing.xfq on 2016/12/5.
 */
public class Message {
    public long versionId;
    public long requestCode;
    public long responseCode;
    public Object returnValue;
    public String methodName;
    public Class<?> returnType;
    public Class<?>[] parameterTypes;
    public Object[] args;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
