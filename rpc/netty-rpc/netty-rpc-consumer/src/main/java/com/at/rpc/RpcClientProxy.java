package com.at.rpc;

import com.at.rpc.spring.reference.RpcInvokerProxy;

import java.lang.reflect.Proxy;

public class RpcClientProxy {

    public <T> T clientProxy(final Class<T> interfaceClazz, final String host, final int port) {
        return (T) Proxy.newProxyInstance(
                interfaceClazz.getClassLoader(),
                new Class<?>[]{interfaceClazz},
                new RpcInvokerProxy(host, port)
        );
    }

}
