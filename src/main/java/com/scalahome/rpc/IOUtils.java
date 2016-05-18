package com.scalahome.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by xufuqing on 16/5/16.
 */
public class IOUtils {

    private static Logger logger = LoggerFactory.getLogger(IOUtils.class);

    public static String getStackTrace(Throwable e) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        e.printStackTrace(new PrintWriter(buffer, true));
        try {
            return buffer.toString("UTF-8");
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            try {
                buffer.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

    public static byte[] longToByteArray(long value) {
        byte[] bytes = new byte[8];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) ((value >> 8 * (bytes.length - i - 1)) & 0xFF);
        }
        return bytes;
    }

    public static long byteArrayToLong(byte[] bytes, int pos) {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value += (bytes[pos + i] & 0x00000000000000FFL) << (8 * (8 - i - 1));
        }
        return value;
    }

    public static byte[] intToByteArray(int value) {
        byte[] bytes = new byte[4];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) ((value >> 8 * (bytes.length - i - 1)) & 0xFF);
        }
        return bytes;
    }

    public static int byteArrayToInt(byte[] bytes, int pos) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            value += (bytes[pos + i] & 0x000000FF) << (8 * (8 - i - 1));
        }
        return value;
    }

    public static byte[] shortToByteArray(short value) {
        byte[] bytes = new byte[2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) ((value >> 8 * (bytes.length - i - 1)) & 0xFF);
        }
        return bytes;
    }

    public static short byteArrayToShort(byte[] bytes, int pos) {
        short value = 0;
        for (int i = 0; i < 2; i++) {
            value += (bytes[pos + i] & 0x00FF) << (8 * (8 - i - 1));
        }
        return value;
    }

}
