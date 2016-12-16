package com.scalahome.rpc;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by fuqing.xfq on 2016/12/16.
 */
public class TimedHashMap<K,V> extends HashMap<K,V> {

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private LinkedList<K>[] queues = new LinkedList[]{new LinkedList<K>(), new LinkedList<K>(), new LinkedList<K>()};
    private int queueIndex;

    private final long timeOutPeriod;

    public TimedHashMap() {
        this(1000);
    }

    public TimedHashMap(long timeOutPeriod) {
        this.timeOutPeriod = timeOutPeriod;
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                int nextQueueIndex = (queueIndex + 1) % 3;
                LinkedList<K> currentQueue = queues[nextQueueIndex];
                while (!currentQueue.isEmpty()) {
                    TimedHashMap.super.remove(currentQueue.removeFirst());
                }
                queueIndex = nextQueueIndex;
            }
        }, TimedHashMap.this.timeOutPeriod, TimedHashMap.this.timeOutPeriod, TimeUnit.MILLISECONDS);
    }

    @Override
    public V put(K key, V value) {
        V v = super.put(key, value);
        queues[queueIndex].add(key);
        return v;
    }
}
