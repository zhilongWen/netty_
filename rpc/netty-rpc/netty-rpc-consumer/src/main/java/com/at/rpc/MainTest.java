package com.at.rpc;

import com.at.rpc.api.IUserService;

public class MainTest {
    public static void main(String[] args) {
        RpcClientProxy rpcClientProxy = new RpcClientProxy();
        IUserService userService = rpcClientProxy.clientProxy(IUserService.class, "127.0.0.1", 8080);
        System.out.println(userService.saveUser("Mic"));
    }
}
