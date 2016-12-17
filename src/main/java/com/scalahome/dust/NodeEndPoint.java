package com.scalahome.dust;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by fuqing.xfq on 2016/12/16.
 */
public interface NodeEndPoint {
    void checkAlive();
    void registerNode(String node);
    void reportNodeDead(String node);
    List<KeyRange> getKeyRangeList(String node);
    StorageUsage getStorageUsage(String node);
    void reportStorageUsageChange(String node, StorageUsage storageUsage);
    void updateStorageUsage(String node, StorageUsage storageUsage);
    void updateStorageUsageMap(Map<String, StorageUsage> stringStorageUsageMap);
    void reportKeyRangeSplit(KeyRange from, List<KeyRange> to);
    void updateMaster(String master, int spreadTimes);
    void updateKeyRange(String node, List<KeyRange> keyRangeList, int spreadTimes);
    void updateKeyRangeMap(Map<String, List<KeyRange>> keyRangeMap, int spreadTimes);
    void updateNodeList(List<String> node, long versionId, int spreadTimes);
    Set<String> getLocations(String key);
    void reportDataStored(String key, String node, int targetReplication);
    void copyData(String key, String fromNode);

}
