package com.xiaomalixing.distributeLock.zookeeper.curatorLock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @date: 16:40 2018/8/20 0020.
 * @author: huanghai
 * @Description:
 */
public class CuratorLock {
    public static void main(String[] args) throws Exception {
        //创建客户端连接
        CuratorFramework curatorFramework=
                CuratorFrameworkFactory.builder()
                        .connectString("localhost:2181,localhost:2182,localhost:2183")
                        .sessionTimeoutMs(4000)
                        .retryPolicy(new ExponentialBackoffRetry(1000,3))
                        .namespace("curator").build();
        curatorFramework.start();

        CountDownLatch latch = new CountDownLatch(1);
        for (int i =0 ; i< 10 ; i++){
            new Thread(() -> {
                InterProcessMutex lock = null;
                try {
                    lock = new InterProcessMutex(curatorFramework, "/locks");
                    latch.await(); //阻塞产生的线程
                    lock.acquire(10000, TimeUnit.SECONDS); //获取锁
                    System.out.println(Thread.currentThread().getName()+"获取锁成功!");

                    System.out.println("各种操作...");
                   // Thread.sleep(3000);

                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    try {
                        if(lock!=null){
                            lock.release();
                            System.out.println(Thread.currentThread().getName()+"========释放锁成功!");
                        }else {
                            System.out.println(Thread.currentThread().getName()+"没有持有锁!");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            },"Thread-"+i).start();
        }
        latch.countDown();//瞬间释放所有的线程

        System.in.read();  //阻塞程序停止
    }
}
