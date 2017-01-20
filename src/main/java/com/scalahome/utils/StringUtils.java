package com.scalahome.utils;

/**
 * Created by fuqing.xfq on 2017/1/5.
 */
public class StringUtils {
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
