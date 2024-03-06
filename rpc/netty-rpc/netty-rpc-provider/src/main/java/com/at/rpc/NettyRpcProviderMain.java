package com.at.rpc;


import com.at.rpc.protocol.NettyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"com.at.rpc.spring", "com.at.rpc.service"})
@SpringBootApplication
public class NettyRpcProviderMain {
    public static void main(String[] args) {
        SpringApplication.run(NettyRpcProviderMain.class, args);

        new NettyServer("127.0.0.1", 8080).start();

    }
}
