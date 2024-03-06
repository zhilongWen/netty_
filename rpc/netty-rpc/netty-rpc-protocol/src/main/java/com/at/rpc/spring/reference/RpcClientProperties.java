package com.at.rpc.spring.reference;


import lombok.Data;

@Data
public class RpcClientProperties {
    private String serviceAddress="192.168.1.102";

    private int servicePort=20880;
}
