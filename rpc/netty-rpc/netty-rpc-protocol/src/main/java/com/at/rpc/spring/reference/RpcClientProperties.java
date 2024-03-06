package com.at.rpc.spring.reference;


import lombok.Data;

@Data
public class RpcClientProperties {
    private String serviceAddress="192.168.43.159";

    private int servicePort=20880;

    private byte registryType;

    private String registryAddress;
}
