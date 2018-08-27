package com.xiaomalixing.distributeLock.redis.redissionLock;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @date: 16:16 2018/8/21 0017.
 * @author: huanghai
 * @Description:
 */
public class RedissionLockTest {
    public static void main(String[] args) throws IOException {
        CountDownLatch latch = new CountDownLatch(1);
        for (int i=0 ; i<10 ; i++){
            new Thread(() -> {
                try {
                    latch.await(); //阻塞产生的线程
                    RedissionLock.acquire("TEST_LOCK"); //获取锁
                    System.out.println(Thread.currentThread().getName()+"获取了锁");

                    Thread.sleep(300);

                    RedissionLock.release("TEST_LOCK");
                    System.out.println(Thread.currentThread().getName()+"释放了锁");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            },"Thread-"+i).start();
        }
        latch.countDown();//瞬间释放所有的线程

        System.in.read();  //阻塞程序停止
    }
}
