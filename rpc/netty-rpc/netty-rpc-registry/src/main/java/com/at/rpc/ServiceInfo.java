package com.at.rpc;

import lombok.Data;

@Data
public class ServiceInfo {
    private String serviceName;
    private String serviceAddress;
    private int servicePort;
}