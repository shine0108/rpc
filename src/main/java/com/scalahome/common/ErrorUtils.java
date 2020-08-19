package com.scalahome.common;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
public class ErrorUtils {

    private ErrorUtils() {}

    public static String getStackTrace(Throwable e) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        e.printStackTrace(new PrintWriter(buffer, true));
        try {
            return buffer.toString("UTF-8");
        } catch (Exception e1) {
            log.error(e1.getMessage(), e1);
        } finally {
            try {
                buffer.close();
            } catch (IOException e1) {
                log.error(e1.getMessage(), e1);
            }
        }
        return null;
    }
}
