package com.scalahome.common;

import com.google.common.cache.Cache;
import com.google.common.collect.ImmutableSet;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.AbstractQueue;
import java.util.Iterator;

/**
 * @author fuqing.xu
 * @date 2020-07-31 14:02
 */
public class GuavaCacheModifier {

    public static void setRecencyQueueAsEmptyQueue(Cache cache) throws NoSuchFieldException, IllegalAccessException {
        Field field = cache.asMap().getClass().getDeclaredField("segments");
        field.setAccessible(true);
        Object[] segments = (Object[]) field.get(cache.asMap());
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        for (Object segment : segments) {
            AbstractQueue emptyQueue = new AbstractQueue<Object>() {
                @Override
                public boolean offer(Object o) {
                    return true;
                }

                @Override
                public Object peek() {
                    return null;
                }

                @Override
                public Object poll() {
                    return null;
                }

                @Override
                public int size() {
                    return 0;
                }

                @Override
                public Iterator<Object> iterator() {
                    return ImmutableSet.of().iterator();
                }
            };
            Field recencyQueue = segment.getClass().getDeclaredField("recencyQueue");
            recencyQueue.setAccessible(true);
            modifiersField.setInt(recencyQueue, field.getModifiers() & ~Modifier.FINAL);
            recencyQueue.set(segment, emptyQueue);
        }
    }
}
