package com.scalahome.common;

import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author fuqing.xu
 * @date 2020-07-23 16:54
 */
public class IndexedResourceManager {

    private final List<Integer> occupiedMsg = new LinkedList<>();

    private final int maxSize;

    public IndexedResourceManager(int maxSize, BitSet bitSet) {
        this.maxSize = maxSize;
        initOccupiedMsg(bitSet);
    }

    private synchronized void initOccupiedMsg(BitSet bitSet) {
        boolean pre = true;
        for (int i = 0; i < bitSet.size(); i++) {
            if (occupiedMsg.isEmpty()) {
                if (bitSet.get(i)) {
                    occupiedMsg.add(i);
                    pre = bitSet.get(i);
                }
            } else if (pre ^ bitSet.get(i)) {
                occupiedMsg.add(i);
                pre = bitSet.get(i);
            }
        }
    }

    public synchronized int allocate() {
        if (occupiedMsg.isEmpty()) {
            return -1;
        }
        int availableIndex = occupiedMsg.remove(0);
        // still have any available node
        if (availableIndex + 1 < maxSize) {
            if (occupiedMsg.isEmpty() || availableIndex + 1 < occupiedMsg.get(0)) {
                occupiedMsg.add(0, availableIndex + 1);
            } else {
                occupiedMsg.remove(0);
            }
        }
        return availableIndex;
    }

    public synchronized void release(int index) {
        Iterator<Integer> iterator = occupiedMsg.iterator();
        for (int i = 0; iterator.hasNext(); i++) {
            Integer next = iterator.next();
            if (next == index) {
                iterator.remove();
                if (iterator.hasNext()) {
                    iterator.next();
                    iterator.remove();
                } else if (index + 1 < maxSize) {
                    occupiedMsg.add(i, index + 1);
                }
                return;
            } else if (next > index) {
                if (next == index + 1) {
                    // replace
                    occupiedMsg.set(i, index);
                } else {
                    // add this one as available node
                    occupiedMsg.add(i, index);
                    // mark the next as not available if needed
                    if (index + 1 < maxSize) {
                        occupiedMsg.add(i + 1, index + 1);
                    }
                }
                return;
            }
        }
        // cant find any available node after this one
        occupiedMsg.add(index);
        // mark the next as not available if needed
        if (index + 1 < maxSize) {
            occupiedMsg.add(index + 1);
        }
    }
}
