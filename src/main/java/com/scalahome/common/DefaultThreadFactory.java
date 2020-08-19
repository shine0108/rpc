package com.scalahome.common;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author fuqing.xu
 * @date 2020-07-09 17:45
 */
public class DefaultThreadFactory implements ThreadFactory {

    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;
    private final boolean daemon;
    private final int priority;

    public DefaultThreadFactory(String namePrefix) {
        this(namePrefix, false, Thread.NORM_PRIORITY);
    }

    public DefaultThreadFactory(String namePrefix, boolean daemon) {
        this(namePrefix, daemon, Thread.NORM_PRIORITY);
    }

    public DefaultThreadFactory(String namePrefix, boolean daemon, int priority) {
        this.namePrefix = namePrefix + "-pool-" + poolNumber.getAndIncrement() + "-thread-";
        this.daemon = daemon;
        this.priority = priority;
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r,
                namePrefix + threadNumber.getAndIncrement(),
                0);
        if (t.isDaemon() ^ daemon) {
            t.setDaemon(daemon);
        }
        if (t.getPriority() != priority) {
            t.setPriority(priority);
        }
        return t;
    }
}
