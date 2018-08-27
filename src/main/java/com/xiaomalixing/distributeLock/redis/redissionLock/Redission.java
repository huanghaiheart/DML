package com.xiaomalixing.distributeLock.redis.redissionLock;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * @date: 14:32 2018/8/21 0017.
 * @author: huanghai
 * @Description:
 */
public class Redission {
    private static RedissonClient REDISSION  ;
    private static Config config = new Config();

    //初始化redis的配置
    private static void init(){
        try {
            config.useSingleServer()
                    .setAddress("redis://localhost:6379")
                 //   .setConnectTimeout(30000)
                    //当与某个节点的连接断开时，等待与其重新建立连接的时间间隔。时间单位是毫秒。默认:3000
                 //   .setReconnectionTimeout(10000)
                    //等待节点回复命令的时间。该时间从命令发送成功时开始计时。默认:3000
                    .setTimeout(10000)
                    //如果尝试达到 retryAttempts（命令失败重试次数） 仍然不能将命令发送至某个指定的节点时，将抛出错误。如果尝试在此限制之内发送成功，则开始启用 timeout（命令等待超时） 计时。默认值：3
                    .setRetryAttempts(5)
                    .setPassword("password")
                    //在一条命令发送失败以后，等待重试发送的时间间隔。时间单位是毫秒。     默认值：1500
                    .setRetryInterval(3000);
            REDISSION = Redisson.create(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取redission客户端
    public static Redisson getRedisClient(){
        init();
        return (Redisson) REDISSION;
    }

}
