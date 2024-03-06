package com.at.rpc.spring.reference;

import com.at.rpc.IRegistryService;
import com.at.rpc.RegistryFactory;
import com.at.rpc.RegistryType;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

public class SpringRpcReferenceBean implements FactoryBean<Object> {

    private Class<?> interfaceClass;
    private Object object;
//    private String serviceAddress;
//    private int servicePort;

    private byte registryType;
    private String registryAddress;

    public void init(){
        //修改增加注册中心
        IRegistryService registryService= RegistryFactory.createRegistryService(
                this.registryAddress,
                RegistryType.findByCode(this.registryType)
        );
        this.object= Proxy.newProxyInstance(this.interfaceClass.getClassLoader(),
                new Class<?>[]{this.interfaceClass},
                new RpcInvokerProxy(registryService));
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

//    public void setServiceAddress(String serviceAddress) {
//        this.serviceAddress = serviceAddress;
//    }

//    public void setServicePort(int servicePort) {
//        this.servicePort = servicePort;
//    }

    public void setRegistryType(byte registryType) {
        this.registryType = registryType;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }

}
