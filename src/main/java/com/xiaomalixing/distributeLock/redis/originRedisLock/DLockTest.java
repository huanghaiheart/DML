package com.xiaomalixing.distributeLock.redis.originRedisLock;

import java.util.concurrent.CountDownLatch;

/**
 * @date: 11:31 2018/8/22 0022.
 * @author: huanghai
 * @Description:
 */
public class DLockTest {
    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i = 0; i < 20; i++) {
            new Thread(()->{
                DLock lock = null;
                try {
                    lock = new DLock("TEST_KEY");
                    countDownLatch.await();
                    lock.lock(0,5000);
                    System.out.println("各种操作。。。");
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    if(lock !=null){
                        lock.close();
                    }
                }
            },"Thred-"+i).start();
        }
        countDownLatch.countDown();
    }
}
