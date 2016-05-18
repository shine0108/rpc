package com.scalahome.rpc;

import java.lang.annotation.*;

/**
 * Created by xufuqing on 16/5/18.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Async {
}
