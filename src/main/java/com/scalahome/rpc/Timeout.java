package com.scalahome.rpc;

import java.lang.annotation.*;

/**
 * @author fuqing.xu
 * @date 2020-07-25 18:55
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface Timeout {
    long value();
}
