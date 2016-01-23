package com.srpc.registry;

import com.srpc.util.Constant;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;


/**
 * Created by zynb0319 on 2016/1/23.
 */
public class ServiceRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistry.class);
    private CountDownLatch latch = new CountDownLatch(1);
    private String registerAddress;
    public ServiceRegistry(String registerAddress){
        this.registerAddress = registerAddress;
    }
    public void register(String data){
        if(data!=null){
            ZooKeeper zk = connectServer();
            if(zk!=null){
                createNode(zk,data);
            }
        }
    }

    private void createNode(ZooKeeper zk, String data) {
        try {
            byte [] bytes = data.getBytes();
            String path = zk.create(Constant.ZK_DATA_PATH,bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            LOGGER.debug("create zookeeper node ({} => {})", path, data);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

        }

    }

    private ZooKeeper connectServer() {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(registerAddress, Constant.ZK_SESSION_TIMEOUT, new Watcher() {
                public void process(WatchedEvent watchedEvent) {
                    if(watchedEvent.getState()==Event.KeeperState.SyncConnected){
                        latch.countDown();
                    }
                }
            });
            latch.wait();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

        }
        return zk;
    }

}
