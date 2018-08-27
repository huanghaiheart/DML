package com.xiaomalixing.distributeLock;

import java.util.concurrent.CountDownLatch;

/**
 * @date: 14:24 2018/8/23 0023.
 * @author: huanghai
 * @Description:
 */
public class CountDownLatchTest {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);


        new Thread(()->{
            try {
                System.out.println("测试CountDownLatch开始");
                System.out.println(Thread.currentThread().getName()+"被CountDownLatch block...");
                countDownLatch.await();
                System.out.println(Thread.currentThread().getName()+"被CountDownLatch release...继续运行");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"Threads-1").start();

        Thread.sleep(3000);

        System.out.println(Thread.currentThread().getName()+"继续运行");
        System.out.println("CountDownLatch调用countDown()方法把num减为0");
       countDownLatch.countDown();



    }
}
