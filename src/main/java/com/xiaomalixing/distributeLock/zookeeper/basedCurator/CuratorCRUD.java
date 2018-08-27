package com.xiaomalixing.distributeLock.zookeeper.basedCurator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * @date: 16:03 2018/8/20 0020.
 * @author: huanghai
 * @Description:
 */
public class CuratorCRUD {
    private static CuratorFramework curatorFramework;

    public static void main(String[] args) throws Exception {
        //1.创建节点
//        createNode();

        //2.获得节点数据
//        getData();

        //3.修改节点数据
//          setData();

        //4.删除节点数据
//        deleteData();
    }

    /**
     * 删除节点数据
     */
    private static void deleteData() throws Exception {
        init();
        curatorFramework
                .delete()
                .deletingChildrenIfNeeded()
                .withVersion(-1)
                .forPath("/persistent/123");
    }

    /**
     * 修改节点数据
     */
    private static void setData() throws Exception {
        init();
        //先获取次节点的数据
        Stat stat = new Stat();
        byte[] data = curatorFramework
                .getData()
                .storingStatIn(stat)
                .forPath("/persistent/123");
        System.out.println(String.format("修改之前的数据为:%s,版本号为:%s",new String(data),stat.getVersion()));
        //在修改节点数据
        stat = curatorFramework
                .setData()
                .withVersion(stat.getVersion())
                .forPath("/persistent/123","huanghai".getBytes());
        System.out.println(String.format("修改之后的数据为:%s,版本号为:%s","huanghai",stat.getVersion()));
    }

    /**
     * 获得节点数据
     */
    private static void getData() throws Exception {
        Stat stat = new Stat();
        byte[] bytes = curatorFramework
                .getData()
                .storingStatIn(stat)
//                .usingWatcher((Watcher) watchedEvent -> System.out.println(watchedEvent.getType()))
                .forPath("/persistent/123");
        System.out.println("此节点数据为"+new String(bytes));
    }

    /**
     *  创建节点
     * @throws Exception
     */
    private static void createNode() throws Exception {
        String s = curatorFramework
                .create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .forPath("/persistent/123", "0".getBytes());
        System.out.println(s);
    }

    /**
     * 初始化连接
     */
    private static void init(){
        curatorFramework=CuratorFrameworkFactory.builder()
                        .connectString("localhost:2181,localhost:2182,localhost:2183")
                        .sessionTimeoutMs(4000)
                        .retryPolicy(new ExponentialBackoffRetry(1000,3))
                        .namespace("curator").build();
        curatorFramework.start();
    }
}
