package com.at.rpc.nio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class NioClient {
    public static void main(String[] args) throws IOException {

        Selector selector = Selector.open();
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);

        boolean connect = socketChannel.connect(new InetSocketAddress("192.168.43.159", 9090));
        if (connect){
            socketChannel.register(selector, SelectionKey.OP_READ);
        }else{
            socketChannel.register(selector,SelectionKey.OP_CONNECT);
        }

        System.out.println("client start...");
        NioClientHandler handler = new NioClientHandler(selector, StandardCharsets.UTF_8);
        handler.start();


    }
}
