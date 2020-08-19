package com.scalahome.rpc.proto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fuqing.xu
 * @date 2020-07-13 18:54
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {
    private int version;
    private Object result;
    private Long requestId;
    private String errMsg;
}
