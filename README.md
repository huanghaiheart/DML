<h1>基于redis,zookeeper实现分布式锁</h1>

<h2>1.铺垫</h2>
countDownLatch的使用[CountDownLatchTest.java]<br/>
zookeeper客户端操作zk节点及watcher事件机制[SimpleOperationDemo.java]<br/>
curator对客户端的封装demo演示[CuratorCRUD.java]<br/>
<h2>2.zk原生客户端实现分布式锁</h2>
代码[OriginZKlock.java]<br/>
分布式锁测试[ZKTest.java]<br/>
<h2>3.使用curator完成可重入分布式互斥锁(InterProcessMutex)</h2>
代码[CuratorLock.java]<br/>
<h2>4.redis的java客户端实现分布式锁</h2>
核心redis方法 setnx(key,val)<br/>
代码[DLock.java]<br/>
测试[DLockTest.java]<br/>
<h2>5.Redisson框架实现分布式可重入互斥锁</h2>
代码[Redission.java]、[RedissionLock.java]<br/>
测试[RedissionLockTest]