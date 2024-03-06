package com.at.rpc.loadbalance;

import com.at.rpc.ServiceInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.x.discovery.ServiceInstance;

import java.util.List;
import java.util.Random;

@Slf4j
public class RandomLoadBalance extends AbstractLoadBanalce{
    @Override
    protected ServiceInstance<ServiceInfo> doSelect(List<ServiceInstance<ServiceInfo>> servers) {

        int size = servers.size();
        Random random = new Random();

        ServiceInstance<ServiceInfo> serviceInstance = servers.get(random.nextInt(size));

        log.info("select serviceInstance = {}",serviceInstance);

        return serviceInstance;
    }
}
