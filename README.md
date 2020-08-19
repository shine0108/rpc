- 介绍：
```
这是一个高性能的RPC框架，压测性能如下：
1）机器配置
Server -- 48 Cores, 128G, 万兆网卡
Client -- 48 Cores, 128G, 万兆网卡

2) 压测接口
ping, Request/Response均为1K数组，Server不做任何操作

3）Server/Client部署情况
Server 单进程
Client 4个进程，每个进程30个Thread

4）压测结果
QPS > 18W, 99.95%延迟低于10ms


技术架构：
1）网络层基于Netty
2）使用CGLib实现动态代理
3）序列化库采用ProtoBuf

可优化点：
1）Java Project Loom加入JDK之后使用协程/纤程
2）序列化部分，可以避免一次内存拷贝，但这可能导致net层和rpc部分的过分耦合，需要想到一个好的方法优雅解决
```