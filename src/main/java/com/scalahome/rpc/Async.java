package com.scalahome.rpc;

import java.lang.annotation.*;

/**
 * Created by xufuqing on 16/5/18.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Async {
}
