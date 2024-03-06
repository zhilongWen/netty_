package com.at.rpc;


import com.at.rpc.zookeeper.ZookeeperRegistryService;

public class RegistryFactory {


    public static IRegistryService createRegistryService(String address,RegistryType registryType){

        IRegistryService registryService = null;

        try {

            switch (registryType){
                case EUREKA :
                    break;
                case ZOOKEEPER:
                    registryService = new ZookeeperRegistryService(address);
                    break;
                default:
                    registryService = new ZookeeperRegistryService(address);
                    break;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return registryService;

    }

}
