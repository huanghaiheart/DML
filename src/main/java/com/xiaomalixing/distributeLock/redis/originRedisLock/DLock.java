package com.xiaomalixing.distributeLock.redis.originRedisLock;

import redis.clients.jedis.Jedis;

/**
 * @date: 10:32 2018/8/22 0017.
 * @author: huanghai
 * @Description:
 */

public class DLock implements AutoCloseable {

    private String PREFIX_KEY = "REDIS_LOCK_";  //锁前缀
    private Jedis JEDIS = null;                 //客户端
    private String KEY;                         //锁的key值
    private int TRY_TIMEOUT = 20;               //没有获得锁，每次睡10ms,这样轮训获取锁
    private boolean LOCK_FLAG = false;          //是否持有锁

    public DLock(String key) {
        KEY = PREFIX_KEY + key;
        JEDIS = new Jedis("localhost",6379);
        JEDIS.auth("password");
    }

    /**
     * 获取锁
     * @param lockTime 持有锁的时间
     * @param timeOut 获取锁超时时间
     */
    public void lock(long lockTime, long timeOut){
        long i=0;
        do {
            //setnx 设置成功返回 1 ,设置失败返回 0
            LOCK_FLAG = (JEDIS.setnx(KEY,"111") == 1);
            if (LOCK_FLAG) { //获得锁成功
                //设置过期时间
                JEDIS.expire(KEY, 5);
                System.out.println(String.format(Thread.currentThread().getName() + "获得锁成功!"));
                break;
            }
            try {
                //没有获取锁，就让其睡眠指定时间，然后轮训的获取锁
                Thread.sleep(TRY_TIMEOUT);
                //在线程在这个方法中的总时间计算
                i += TRY_TIMEOUT;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while(i < timeOut);
    }


    /**
     * 释放锁
     */
    public void close()  {
        if(LOCK_FLAG && JEDIS.get(KEY) != null) { //如果持有锁并且锁还没有过期
            //删除对应的key
            JEDIS.del(KEY);
            System.out.println("线程" + Thread.currentThread().getName() + "解锁成功!key=" + KEY);
            JEDIS.close();
        } else {
            throw new RuntimeException(Thread.currentThread().getName() + "，关闭锁时发现没有获取到锁");
        }
    }
    
}
