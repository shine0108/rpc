package com.scalahome.rpc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by xufuqing on 16/4/12.
 */
public class DecoratedHashMap<K,V> implements Map<K,V> {
    private HashMap<K,V> core = new HashMap<K, V>();

    public int size() {
        return core.size();
    }

    public boolean isEmpty() {
        return core.isEmpty();
    }

    public boolean containsKey(Object key) {
        return core.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return core.containsValue(value);
    }

    public V get(Object key) {
        return core.get(key);
    }

    public synchronized V put(K key, V value) {
        HashMap<K, V> newCore = new HashMap<K, V>(core);
        V v = newCore.put(key, value);
        core = newCore;
        return v;
    }

    public synchronized V remove(Object key) {
        HashMap<K, V> newCore = new HashMap<K, V>(core);
        V v = newCore.remove(key);
        core = newCore;
        return v;
    }

    public synchronized void putAll(Map<? extends K, ? extends V> m) {
        HashMap<K, V> newCore = new HashMap<K, V>(core);
        newCore.putAll(m);
        core = newCore;
    }

    public synchronized void clear() {
        HashMap<K, V> newCore = new HashMap<K, V>(core);
        newCore.clear();
        core = newCore;
    }

    public Set<K> keySet() {
        return core.keySet();
    }

    public Collection<V> values() {
        return core.values();
    }

    public Set<Entry<K, V>> entrySet() {
        return core.entrySet();
    }

    @Override
    public String toString() {
        return core.toString();
    }

    @Override
    public int hashCode() {
        return core.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return core.equals(obj);
    }
}
