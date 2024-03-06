package com.at.rpc.spring.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "gp.rpc")
public class RpcServerProperties {

    private int servicePort;

    private byte registerType;

    private String registryAddress;
}