package com.scalahome.service.demo;

import com.scalahome.service.Params;
import com.scalahome.service.Path;

/**
 * Created by fuqing.xfq on 2017/1/3.
 */
@Path("person")
public interface Person {
    @Params("name")
    void setName(String name);
    String getName();
}
