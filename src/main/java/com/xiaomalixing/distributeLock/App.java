package com.xiaomalixing.distributeLock;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.CountDownLatch;

/**
 * Hello world!
 *
 */
public class App 
{
    public static Integer PRODUCT_NUM = 10;    //有10件商品
    public static String KEY = "PRODUCT_NUM";  //在redis中的key值
    public static JedisPool jedisPool;         //jedis的连接池
    public static CountDownLatch countDownLatch = new CountDownLatch(2);  //线程间通信用，更方便的控制线程切换
    private static Object obj = new Object();  //全局锁1
    private static Object obj1 = new Object();  //全局锁2
    private Jedis jedis ; //jedis客户端

    static {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxIdle(5);
            config.setMaxWaitMillis(1000 * 100);
            config.setMaxTotal(1000);
            jedisPool = new JedisPool(config,"localhost",6379);

            //设置初始值
            Jedis jedis1 = jedisPool.getResource();
            jedis1.auth("password");
            jedis1.set(KEY,String.valueOf(PRODUCT_NUM));
            System.out.println("设置初始值成功!");
            jedis1.close();
    }

    public App() {
        jedis = jedisPool.getResource();
        jedis.auth("huanghai");
    }

    public static void main(String[] args ) throws InterruptedException {

        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                try {
                    App app = new App();

                    countDownLatch.await();

                    app.buyProduct();
                    app.jedis.close();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            },"Thread-"+i).start();
        }
        countDownLatch.countDown();


   /*    for (int i = 20; i < 40; i++) {
            new Thread(() -> {
                try {
                    App app = new App();

                    countDownLatch.await();

                    app.buyProduct1();
                    app.jedis.close();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            },"Thread-"+i).start();
        }
*/
        countDownLatch.countDown();
    }


    public void buyProduct(){
       synchronized (obj){
            String value = jedis.get(KEY);
            int num = Integer.parseInt(value);
            if(num > 0){
                num -- ;
                System.out.println(Thread.currentThread().getName()+"抢购商品成功!商品还剩数量num="+num);
                jedis.set(KEY,String.valueOf(num));
            }else {
                System.out.println(Thread.currentThread().getName()+"抢购商品失败!");
            }
       }
    }

    public void buyProduct1(){
        synchronized (obj1){
            String value = jedis.get(KEY);
            int num = Integer.parseInt(value);
            if(num > 0){
                num -- ;
                System.out.println(Thread.currentThread().getName()+"抢购商品成功!商品还剩数量num="+num);
                jedis.set(KEY,String.valueOf(num));
            }else {
                System.out.println(Thread.currentThread().getName()+"抢购商品失败!");
            }
        }
    }

}
