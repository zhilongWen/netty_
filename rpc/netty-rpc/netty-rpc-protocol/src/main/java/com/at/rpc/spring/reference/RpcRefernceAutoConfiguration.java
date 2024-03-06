package com.at.rpc.spring.reference;

import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class RpcRefernceAutoConfiguration implements EnvironmentAware {

    @Bean
    public SpringRpcReferencePostProcessor postProcessor(){
        String address=environment.getProperty("gp.serviceAddress");
        int port=Integer.parseInt(environment.getProperty("gp.servicePort"));
        RpcClientProperties rc=new RpcClientProperties();
        rc.setServiceAddress(address);
        rc.setServicePort(port);
        return new SpringRpcReferencePostProcessor(rc);
    }

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment=environment;
    }

}
