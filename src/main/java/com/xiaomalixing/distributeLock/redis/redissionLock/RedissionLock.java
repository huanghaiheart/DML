package com.xiaomalixing.distributeLock.redis.redissionLock;

import org.redisson.Redisson;
import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;

/**
 * @date: 16:10 2018/8/21 0017.
 * @author: huanghai
 * @Description:
 */
public class RedissionLock {
    private static Redisson redLock = Redission.getRedisClient();
    private static final String LOCK_KEY = "REDIS_KEY_";

    /**
     * 根据name对进行上锁操作，redissonLock 阻塞事的，采用的机制发布/订阅
     * @param lockname
     */
    public static void acquire(String lockname){
        String key = LOCK_KEY + lockname;
        RLock lock = redLock.getLock(key);
        //lock提供带timeout参数，timeout结束强制解锁，防止死锁 ：1分钟
        lock.lock(5, TimeUnit.SECONDS);
    }

    /**
     * 根据name对进行解锁操作
     * @param lockname
     */
    public static void release(String lockname){
        String key = LOCK_KEY + lockname;
        RLock lock = redLock.getLock(key);
        lock.unlock();
    }
}
