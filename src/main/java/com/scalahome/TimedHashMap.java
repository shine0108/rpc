package com.scalahome;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by fuqing.xfq on 2016/12/16.
 */
public class TimedHashMap<K,V> extends HashMap<K,V> {

    private final long timeOut;

    private ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);

    private Set<K> toRemoveKeySet = new HashSet<K>();

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public TimedHashMap() {
        this(1000);
    }

    public TimedHashMap(long timeOut) {
        this.timeOut = timeOut;
        pool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                lock.writeLock().lock();
                try {
                    for(K k : toRemoveKeySet) {
                        TimedHashMap.super.remove(k);
                    }
                    toRemoveKeySet.clear();
                } finally {
                    lock.writeLock().unlock();
                }
            }
        }, timeOut, timeOut, TimeUnit.MILLISECONDS);
    }


    @Override
    public V put(final K key, V value) {
        V v = super.put(key, value);
        lock.readLock().lock();
        try {
            toRemoveKeySet.add(key);
        } finally {
            lock.readLock().unlock();
        }
        return v;
    }
}
