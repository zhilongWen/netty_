package com.at.rpc;

import com.at.rpc.spring.reference.RpcInvokerProxy;

import java.lang.reflect.Proxy;

public class RpcClientProxy {

    public <T> T clientProxy(final Class<T> interfaceClazz, final String host, final int port) {
        IRegistryService registryService= RegistryFactory.createRegistryService(
                "10.211.55.102:2181",
                RegistryType.findByCode((byte) 0)
        );
        return (T) Proxy.newProxyInstance(
                interfaceClazz.getClassLoader(),
                new Class<?>[]{interfaceClazz},
                new RpcInvokerProxy(registryService)
        );
    }

}
