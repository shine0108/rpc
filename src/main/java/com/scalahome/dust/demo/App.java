package com.scalahome.dust.demo;

/**
 * Created by fuqing.xfq on 2016/12/17.
 */
public class App {
    public static void main(String[] args) {

        /**
         * 1，维护集群的探活
         * 2，更新MetaData Key的Range
         * 3，
         */

        /**
         * 1, 相邻节点相互心跳，侦测到死亡之后需要通知Master，发现与Master失去联系则选择自杀
         * 2，每个节点既保存元数据又保存真实数据
         * 3，
         */

        /**
         * MetaData Key的Range
         * 1，Master从所有MetaData收集KeyRange信息
         * 2，Master是ShareNothing的
         * 3，NodeList存储在ETCD
         * 4，KeyRange维护在各个Node上
         */

        /**
         * 节点启动过程：
         * 1，查看是否有Master，竞选Master
         * 2，向Master注册自己
         * 3，Master向其他节点分发这种变化
         */
    }
}
