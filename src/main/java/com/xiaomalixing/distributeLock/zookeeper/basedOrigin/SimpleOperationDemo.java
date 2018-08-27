package com.xiaomalixing.distributeLock.zookeeper.basedOrigin;


import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @date: 17:27 2018/8/17 0017.
 * @author: huanghai
 * @Description: zkclient对节点的CURD,以及watcher机制
 */
public class SimpleOperationDemo  {

    private static ZooKeeper zk ;
    private static final String connectString = "localhost:2181,localhost:2182,localhost:2183";
    private static final Integer sessionTimeOut = 4000;

     public static void main(String[] args) throws Exception {
         //1.合理初始化zk测试
//         initTest();

         //2.不合理初始化测试
//         init1Test();

        //3.创建一个持久化节点节点
//        createNode();

        //4.创建一个临时节点
//        createTempNode();

        //5.创建临时有序节点
//        createTempSeqNode();

        //6.获得一个节点的值
//         getNodeValue();

         //7.修改一个节点的值
//         setNodeData();

         //8.删除一个节点
//         delNode();

         //9.1 zk的watch机制
         // 对节点绑定事件 exist() ; getChildren() ; getData()  事件绑定是一次性的  如果想要多次通知事件就要多次绑定
         // 触发事件的方法:事务操作  对节点的  增  删  改

//         nodeWatcher();

         //9.2 zk绑定事件2
         nodeWatcher2();
     }

    /**
     * 持久绑定事件
     * @throws KeeperException
     * @throws InterruptedException
     * @throws IOException
     */
    private static void nodeWatcher2() throws KeeperException, InterruptedException, IOException {
        init();
        zk.exists("/huanghai", new Mywatch(zk));

        System.in.read();
        zk.close();
    }

    /**
     * watch机制
     * @throws IOException
     * @throws InterruptedException
     * @throws KeeperException
     */
    private static void nodeWatcher() throws IOException, InterruptedException, KeeperException {
        init();
        zk.exists("/huanghai", watchedEvent -> System.out.println(watchedEvent.getType()));

        System.in.read();
        zk.close();
    }

    /**
     * 删除某个节点
     * @throws IOException
     * @throws InterruptedException
     * @throws KeeperException
     */
    private static void delNode() throws IOException, InterruptedException, KeeperException {
         init();
         //第一种删除，根据版本号进行删除
        Stat stat = new Stat();
        byte[] data = zk.getData("/test", false, stat);
        System.out.println(String.format("/xmlx节点当前的值为:%s;版本号为:%s",new String(data),stat.getVersion()));

        zk.delete("/test",stat.getVersion());

        //第二种删除，不管三七二十一，直接删除
     //   zk.delete("/xmlx",-1);

        System.in.read();
        zk.close();

    }

    /**
     * 修改节点的值
     * @throws IOException
     * @throws InterruptedException
     * @throws KeeperException
     */
    private static void setNodeData() throws IOException, InterruptedException, KeeperException {
        init();
        Stat stat = new Stat();
        byte[] data = zk.getData("/xmlx", false, stat);
        System.out.println(String.format("/xmlx节点当前的值为:%s;版本号为:%s",new String(data),stat.getVersion()));

        stat = zk.setData("/xmlx", "new Value1".getBytes(), stat.getVersion());
        System.out.println(String.format("/xmlx节点修改后的值为:%s;修改后的版本号为:%s",new String(data),stat.getVersion()));

        System.in.read();
        zk.close();
    }

    /**
     * 获取一个节点的值
     * @throws IOException
     * @throws InterruptedException
     * @throws KeeperException
     */
    private static void getNodeValue() throws IOException, InterruptedException, KeeperException {
        init();
        Stat stat = new Stat();
        byte[] data = zk.getData("/xmlx", false, stat);
        System.out.println(new String(data));

        zk.close();
    }

    /**
     * 创建临时有序节点
     * @throws IOException
     * @throws InterruptedException
     * @throws KeeperException
     */
    private static void createTempSeqNode() throws IOException, InterruptedException, KeeperException {
        init();
        for (int i = 0; i < 5; i++) {
            String s = zk.create("/test/", (i+"").getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println(String.format("创建临时节点成功!,路径为:%s",s));
        }

        System.in.read();
        zk.close();
    }

    /**
     * 创建临时节点
     * @throws IOException
     * @throws InterruptedException
     * @throws KeeperException
     */
    private static void createTempNode() throws IOException, InterruptedException, KeeperException {
        init();
        String s = zk.create("/xmlx/temp", "0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        System.out.println(String.format("创建临时节点成功!,路径为:%s",s));

        System.in.read();
        zk.close();
    }

    /**
     * 创建一个持久化节点
     * @throws IOException
     * @throws InterruptedException
     * @throws KeeperException
     */
    private static void createNode() throws IOException, InterruptedException, KeeperException {
        init();
        String path = zk.create("/xmlx/huanghai", "0".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println(path);
        zk.close();
    }

    /**
     * 合理的初始化zk客户端
     * @throws IOException
     * @throws InterruptedException
     */
    private static void init() throws IOException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        //zk客户端和服务端连接的4中状态  Disconnected  Connecting  Connected  Close
        zk = new ZooKeeper(connectString, sessionTimeOut, watchedEvent -> {
                  if(watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected){
                      System.out.println("zkClient已经和服务器连接");
                      countDownLatch.countDown();
                  }
        });
        //.....
        countDownLatch.await();
    }

    /**
     * 不合理的初始化zk客户端
     * @throws IOException
     * @throws InterruptedException
     */
    private static void init1() throws IOException {
        zk = new ZooKeeper(connectString, sessionTimeOut, watchedEvent -> {
            if(watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected){
                System.out.println("zkClient已经和服务器连接");
            }
        });
    }

    /**
     * 合理的初始化测试
     * @throws IOException
     * @throws InterruptedException
     */
   private static void initTest() throws IOException, InterruptedException {
       init();
       System.out.println(zk.getState());
       System.out.println("各种操作。。。");

       System.in.read();
   }
    /**
     * 合理的初始化测试
     * @throws IOException
     * @throws InterruptedException
     */
    private static void init1Test() throws IOException, InterruptedException {
        init1();
        System.out.println(zk.getState());
        System.out.println("各种操作。。。");

        System.in.read();
    }

    /**
     * 获得zk客户端实例
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static ZooKeeper getInstance() throws IOException, InterruptedException {
        init();
        return zk;
    }


    static class Mywatch implements Watcher{
        private  ZooKeeper zk;

        public Mywatch(ZooKeeper zk) {
            this.zk = zk;
        }

        @Override
        public void process(WatchedEvent watchedEvent) {
            System.out.println(watchedEvent.getType());
            try {
                zk.exists("/huanghai",this);
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
