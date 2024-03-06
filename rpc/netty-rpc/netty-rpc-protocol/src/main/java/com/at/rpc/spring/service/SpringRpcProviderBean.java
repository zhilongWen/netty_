package com.at.rpc.spring.service;

import com.at.rpc.protocol.NettyServer;
import com.at.rpc.spring.annotation.GpRemoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
public class SpringRpcProviderBean implements InitializingBean, BeanPostProcessor {

    private final int serverPort;
    private final String serverAddress;
    public SpringRpcProviderBean(int serverPort) throws UnknownHostException {
        this.serverPort = serverPort;
        InetAddress address= InetAddress.getLocalHost();
        this.serverAddress=address.getHostAddress();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("begin deploy Netty Server to host {},on port {}",this.serverAddress,this.serverPort);
        new Thread(()->{
            try {
                new NettyServer(this.serverAddress,this.serverPort).start();
            } catch (Exception e) {
                log.error("start Netty Server Occur Exception,",e);
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean.getClass().isAnnotationPresent(GpRemoteService.class)){ //针对存在该注解的服务进行发布
            Method[] methods=bean.getClass().getDeclaredMethods();
            for(Method method: methods){ //保存需要发布的bean的映射
                String key=bean.getClass().getInterfaces()[0].getName()+"."+method.getName();
                BeanMethod beanMethod=new BeanMethod();
                beanMethod.setBean(bean);
                beanMethod.setMethod(method);
                Mediator.beanMethodMap.put(key,beanMethod);
            }
        }
        return bean;
    }
}
