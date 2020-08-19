package com.scalahome.common;

import java.util.*;
import java.util.function.Function;

public class CopyOnWriteMap<K, V> implements Map<K, V> {

    private Map<K, V> internalMap = new HashMap<>();

    @Override
    public int size() {
        return internalMap.size();
    }

    @Override
    public boolean isEmpty() {
        return internalMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return internalMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return internalMap.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return internalMap.get(key);
    }

    @Override
    public V computeIfAbsent(K key,
                             Function<? super K, ? extends V> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        V v;
        if ((v = get(key)) == null) {
            synchronized (this) {
                if ((v = get(key)) == null) {
                    V newValue;
                    if ((newValue = mappingFunction.apply(key)) != null) {
                        put(key, newValue);
                        return newValue;
                    }
                }
            }
        }
        return v;
    }

    @Override
    public V putIfAbsent(K key, V value) {
        V v = get(key);
        if (v == null) {
            synchronized (this) {
                v = get(key);
                if (v == null) {
                    v = put(key, value);
                }
            }
        }
        return v;
    }

    @Override
    public synchronized V put(K key, V value) {
        Map<K, V> newMap = new HashMap<>(internalMap);
        V val = newMap.put(key, value);
        internalMap = newMap;
        return val;
    }

    @Override
    public synchronized V remove(Object key) {
        Map<K, V> newMap = new HashMap<>(internalMap);
        V val = newMap.remove(key);
        internalMap = newMap;
        return val;
    }

    @Override
    public synchronized void putAll(Map<? extends K, ? extends V> m) {
        Map<K, V> newMap = new HashMap<>(internalMap);
        newMap.putAll(m);
        internalMap = newMap;
    }

    @Override
    public synchronized void clear() {
        internalMap = new HashMap<>();
    }

    @Override
    public Set<K> keySet() {
        return internalMap.keySet();
    }

    @Override
    public Collection<V> values() {
        return internalMap.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return internalMap.entrySet();
    }

    @Override
    public String toString() {
        return internalMap.toString();
    }
}
