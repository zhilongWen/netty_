package com.at.rpc;

import org.apache.curator.utils.DefaultZookeeperFactory;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class RegisterTest {
    public static void main(String[] args) throws Exception {

        IRegistryService registryService = RegistryFactory.createRegistryService(
                "10.211.55.102:2181",
                RegistryType.ZOOKEEPER
        );

        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setServiceAddress("127.0.0.1");
        serviceInfo.setServicePort(8080);
        serviceInfo.setServiceName("com.example.RegisterTest");

        registryService.register(serviceInfo);

        System.out.println("register success");

        Thread.sleep(1000);

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {

                ZooKeeper zooKeeper = null;
                try {
                    zooKeeper = new DefaultZookeeperFactory()
                            .newZooKeeper(
                                    "10.211.55.102:2181",
                                    5000,
                                    new Watcher() {
                                        @Override
                                        public void process(WatchedEvent watchedEvent) {

                                        }
                                    },
                                    false
                            );
                    zooKeeper.delete("/registry",0);

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }finally {
                    if (zooKeeper != null){
                        try {
                            zooKeeper.close();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }




            }
        }));


    }
}
