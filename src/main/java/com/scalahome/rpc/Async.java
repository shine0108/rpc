package com.scalahome.rpc;

import java.lang.annotation.*;

/**
 * @author fuqing.xu
 * @date 2020-07-13 19:54
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface Async {
}
