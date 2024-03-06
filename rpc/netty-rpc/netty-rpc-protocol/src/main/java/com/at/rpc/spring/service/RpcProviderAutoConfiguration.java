package com.at.rpc.spring.service;

import com.at.rpc.IRegistryService;
import com.at.rpc.RegistryFactory;
import com.at.rpc.RegistryType;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.UnknownHostException;

@Configuration
@EnableConfigurationProperties(RpcServerProperties.class)
public class RpcProviderAutoConfiguration {

    @Bean
    public SpringRpcProviderBean rpcProviderBean(RpcServerProperties rpcServerProperties) throws UnknownHostException {
        //添加注册中心
        IRegistryService registryService= RegistryFactory.createRegistryService(
                rpcServerProperties.getRegistryAddress(),
                RegistryType.findByCode(rpcServerProperties.getRegisterType())
        );
//        return new SpringRpcProviderBean(rpcServerProperties.getServicePort());
        return new SpringRpcProviderBean(rpcServerProperties.getServicePort(),registryService);
    }
}