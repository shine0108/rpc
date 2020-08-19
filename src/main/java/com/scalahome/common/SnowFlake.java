package com.scalahome.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class SnowFlake {
    // 2019-01-01 00:00:00 UTC+8
    private final static long START_STMP = 1546272000000L;
    private final static long FLAG_BIT = 1;
    private final static long TIMESTAMP_BIT = 41;
    private final static long MACHINE_BIT = 10;
    private final static long SEQUENCE_BIT = 12;
    // max of each
    private final static long MAX_TIMESTAMP = 2L << (TIMESTAMP_BIT - 1);
    private final static long MAX_MACHINE_NUM = 2L << (MACHINE_BIT - 1);
    private final static long MAX_SEQUENCE = 2L << (SEQUENCE_BIT - 1);
    // shift of each
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long TIMESTMP_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private long machineId;
    private final AtomicLong sequence = new AtomicLong(0L);

    private static SnowFlake instance;

    public static SnowFlake getInstance() {
        if (instance == null) {
            synchronized (SnowFlake.class) {
                if (instance == null) {
                    instance = new SnowFlake();
                }
            }
        }
        return instance;
    }

    public static SnowFlake getInstance(long machineId) {
        if (instance == null) {
            synchronized (SnowFlake.class) {
                if (instance == null) {
                    instance = new SnowFlake(machineId);
                }
            }
        }
        return instance;
    }

    public SnowFlake() {
        this(getMacList().hashCode());
    }

    private static String getMacList() {
        StringBuilder buffer = new StringBuilder();
        try {
            Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
            while (enumeration.hasMoreElements()) {
                NetworkInterface networkInterface = enumeration.nextElement();
                if (networkInterface != null && networkInterface.getHardwareAddress() != null) {
                    buffer.append(Hex.encodeHexString(networkInterface.getHardwareAddress()));
                }
            }
        } catch (SocketException e) {
            log.error(e.getMessage(), e);
        }
        if (buffer.length() == 0) {
            buffer.append(UUID.randomUUID().toString());
        }
        return buffer.toString();
    }

    public SnowFlake(long machineId) {
        this.machineId = Math.abs(machineId) % MAX_MACHINE_NUM;
    }

    public long nextId() {
        long timestamp = (System.currentTimeMillis() - START_STMP) % MAX_TIMESTAMP;
        long sequenceId = sequence.getAndIncrement() % MAX_SEQUENCE;
        return timestamp << TIMESTMP_LEFT
                | machineId << MACHINE_LEFT
                | sequenceId;
    }
}