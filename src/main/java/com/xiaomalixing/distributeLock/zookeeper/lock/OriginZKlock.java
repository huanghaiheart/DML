package com.xiaomalixing.distributeLock.zookeeper.lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @date: 11:54 2018/8/20 0020.
 * @author: huanghai
 * @Description:  利用zk的节点特性和watcher机制实现分布式锁
 */
public class OriginZKlock implements Lock,Watcher {

    private ZooKeeper zk;
    private String LOCK_ROOT_NODE = "/locks";
    private String CURRENT_NODE;
    private String WAIT_NODE;

    private CountDownLatch countDownLatch;

    public OriginZKlock(){
        CountDownLatch count = new CountDownLatch(1);
        //zk客户端和服务端连接的4中状态  Disconnected  Connecting  Connected  Close
        try {
            zk = new ZooKeeper("localhost:2181,localhost:2182,localhost:2183", 4000, watchedEvent -> {
                if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
                    System.out.println("zkClient已经和服务器连接");
                    try {
                        Stat stat = zk.exists(LOCK_ROOT_NODE, false);
                        if(stat == null){
                            zk.create(LOCK_ROOT_NODE,"0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    count.countDown();
                }
            });
            System.out.println("正在建立和zk的连接中...");
            count.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取锁
     */
    @Override
    public void lock() {
        if(tryLock()){
            System.out.println(Thread.currentThread().getName()+"获得锁成功!========对应节点为:"+CURRENT_NODE);
            return;
        }
        waitForPer(WAIT_NODE);
    }

    /**
     * 未获取锁的线程进入此方法进行阻塞，并等待前一个节点释放锁
     * @param pre
     */
    private void waitForPer(String pre) {
        try {
            Stat preNode = zk.exists(pre, this);
            if(preNode!=null){
                countDownLatch = new CountDownLatch(1);
                countDownLatch.await(); //阻塞状态
            }
            System.out.println(Thread.currentThread().getName()+"获得锁成功!========对应节点为:"+CURRENT_NODE);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 尝试获取锁
     * @return
     */
    @Override
    public boolean tryLock() {
        try {
            //每个线程进入不管三七二十一，先对对应的线程创建一个临时有序的锁节点
            CURRENT_NODE = zk.create(LOCK_ROOT_NODE + "/", "0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println(Thread.currentThread().getName()+"======尝试获取锁,对其创建节点路径为:"+CURRENT_NODE);
            //然后获取所有的锁节点，判断是否为第一个节点，如果是--》获取锁成功，如果不是---》对其上一个节点进行监听，并等其释放(阻塞)，然后获得锁成功
            List<String> children = zk.getChildren(LOCK_ROOT_NODE, false);
            SortedSet<String> sortedSet = new TreeSet<>();
            for (String child : children) {
                sortedSet.add(LOCK_ROOT_NODE+"/"+child);
            }
            //取第一个节点
            if(sortedSet.first().equals(CURRENT_NODE)){
                return true;
            }
            //取CURRENT_NODE 的上一个节点，并监听它，等待其释放
            SortedSet<String> headSet = sortedSet.headSet(CURRENT_NODE);
            if(headSet!=null){
                WAIT_NODE = headSet.last();
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 释放锁
     */
    @Override
    public void unlock() {
        System.out.println(Thread.currentThread().getName()+"========释放锁!");
        try {
            zk.delete(CURRENT_NODE,-1);
            CURRENT_NODE = null;
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public Condition newCondition() {
        return null;
    }
    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    /**
     * 监听上一个节点的watcher事件
     * @param watchedEvent
     */
    @Override
    public void process(WatchedEvent watchedEvent) {
        if(watchedEvent.getType() == Event.EventType.NodeDeleted){
            if(countDownLatch!=null){
                countDownLatch.countDown();
            }
        }
    }
}
