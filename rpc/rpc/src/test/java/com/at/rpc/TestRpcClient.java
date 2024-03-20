package com.at.rpc;

import com.at.rpc.client.RpcClient;
import com.at.rpc.client.RpcClientProcess;
import com.google.common.hash.Hashing;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class TestRpcClient {
    public static void main(String[] args) throws InterruptedException {

//        RpcClient rpcClient = new RpcClient("192.168.43.159", 9909);

        RpcClientProcess process = new TestRpcClientProcess();

        RpcClient rpcClient = new RpcClient("192.168.43.159", 9909, process);

        System.out.println("TestRpcClient");
//        process.send("client msg");

//        Object ask = process.ask(111L, "clccccccc");
//        System.out.println("ask = " + ask);

        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String msg = scanner.nextLine();
            long requestId = Hashing.murmur3_128().hashString(msg, StandardCharsets.UTF_8).asLong();
            System.out.println("requestId = " + requestId);
            if (msg.contains("1")) {
                Object ask = process.ask(requestId, msg);
                System.out.println("ask = " + ask);
            } else if (msg.contains("2")) {
                Object ask = process.ask(requestId, msg);
                System.out.println("ask = " + ask);
            } else {
                process.send(msg);
            }

        }

    }
}

class TestRpcClientProcess extends RpcClientProcess {
    @Override
    public void process(ChannelHandlerContext ctx, MessageProtocol msg) {
//        System.out.println("msg = " + msg);
    }
}
