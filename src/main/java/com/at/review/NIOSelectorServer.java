package com.at.review;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @create 2022-05-05
 */
public class NIOSelectorServer {

    public static void main(String[] args) throws Exception {

        // 获取 selector
        Selector selector = Selector.open();

        // 服务器启动 socket
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress("127.0.0.1", 8090));

        // 设置为非阻塞模式
        serverSocketChannel.configureBlocking(false);

        // 将 channel 注册到 selector
        // selector 关心 事件为 OP_ACCEPT
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);


        System.out.println("selector 上注册的所有 channel size = " + selector.keys().size());


        while (true) {

            // channel 无事件
            if (selector.select(1000) == 0) {
                System.out.println("server 端等待了1s，无连接...");
                continue;
            }

            // channel 有事件

            // 获取所有有事件的 channel
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();

            // 遍历所有 channel 根据 channel 的事件类型进行处理
            while (keyIterator.hasNext()) {

                SelectionKey key = keyIterator.next();

                // 连接事件
                if (key.isAcceptable()) {

                    // 这是一个新连接 为其创建一个 channel
                    SocketChannel socketChannel = serverSocketChannel.accept();

                    System.out.println("client 端 port =" + socketChannel.getRemoteAddress() + " 连接成功，并为其生成一个 channel：" + socketChannel.hashCode());

                    // 设置为非阻塞模式
                    socketChannel.configureBlocking(false);

                    // 将 socketChannel 注册到 selector，并将通道关注事件设置为 OP_READ 同时为通道创建一个 buffer
                    socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));

                }


                // 读事件
                if (key.isReadable()) {

                    // 从 buffer 中读取数据

                    // 获取 key 关联的 channel
                    SocketChannel channel = (SocketChannel) key.channel();

                    // 获取 key 关联的 buffer
                    ByteBuffer buffer = (ByteBuffer) key.attachment();


                    channel.read(buffer);

                    System.out.println("from client port = " + channel.getRemoteAddress() + " 读取到的数据：" + new String(buffer.array()));


                }


                // 移除处理完的 channel
                keyIterator.remove();

            }


        }


    }

}
