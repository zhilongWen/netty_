package com.at.rpc.nio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.charset.Charset;

public class NioServer {

    private Selector selector;
    private ServerSocketChannel socketChannel;

    public static void main(String[] args) throws IOException {
        new NioServer().bind(9090);
    }

    public void bind(int port) {
        try {
            selector = Selector.open();
            socketChannel = ServerSocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.socket().bind(new InetSocketAddress(port),1024);
            socketChannel.register(selector,SelectionKey.OP_ACCEPT);
            System.out.println("server start...");
            NioServerHandler handler = new NioServerHandler(selector, Charset.forName("utf-8"));
            handler.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
