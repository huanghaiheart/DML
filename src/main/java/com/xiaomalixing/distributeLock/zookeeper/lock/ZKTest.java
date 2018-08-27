package com.xiaomalixing.distributeLock.zookeeper.lock;

import java.util.concurrent.CountDownLatch;

/**
 * @date: 15:23 2018/8/20 0020.
 * @author: huanghai
 * @Description:
 */
public class ZKTest {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        for (int i = 0; i < 10; i++) {
          new Thread(() -> {
              try {
                  OriginZKlock lock = new OriginZKlock();
                  latch.await();
                  lock.lock();
                  System.out.println("各种操作=======");
                  //lock.unlock();
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }
          },"Thread"+i).start();
        }
        latch.countDown();
    }
}
