package com.at.rpc.spring.reference;

import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

public class SpringRpcReferenceBean implements FactoryBean<Object> {

    private Class<?> interfaceClass;
    private Object object;
    private String serviceAddress;
    private int servicePort;

    public void init(){
        this.object= Proxy.newProxyInstance(this.interfaceClass.getClassLoader(),
                new Class<?>[]{this.interfaceClass},
                new RpcInvokerProxy(this.serviceAddress,this.servicePort));
    }

    @Override
    public Object getObject() throws Exception {
        return this.object;
    }

    @Override
    public Class<?> getObjectType() {
        return this.interfaceClass;
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }

}
