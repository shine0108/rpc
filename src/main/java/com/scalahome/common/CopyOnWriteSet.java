package com.scalahome.common;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;

/**
 * @author fuqing.xu
 * @date 2020-07-14 10:38
 */
public class CopyOnWriteSet<E> extends AbstractSet<E> {

    private final Map<E, Object> map = new CopyOnWriteMap<>();
    private static final Object PRESENT = new Object();

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return map.containsKey(o);
    }

    @Override
    public boolean add(E e) {
        return map.put(e, PRESENT) == null;
    }

    @Override
    public boolean remove(Object o) {
        return map.remove(o) == PRESENT;
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }

}
