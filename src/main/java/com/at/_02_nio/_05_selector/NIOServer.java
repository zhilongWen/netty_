package com.at._02_nio._05_selector;


import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @create 2022-04-17
 */
public class NIOServer {

    public static void main(String[] args) throws Exception {

        Selector selector = Selector.open();

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.socket().bind(new InetSocketAddress(9098));

        //设置为非阻塞
        serverSocketChannel.configureBlocking(false);


        //把 serverSocketChannel 注册到  selector 关心 事件为 OP_ACCEPT
        SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("注册后的selectionkey 数量=" + selector.keys().size()); // 1

        while (true){

            //等待1秒，如果没有事件发生, 返回
            if(selector.select(1000) == 0){
                System.out.println("server 端等待了1s，无连接...");
                continue;
            }

            //获取有事件行为的channel
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> selectionKeyIterator = selectionKeys.iterator();


            while (selectionKeyIterator.hasNext()){

                SelectionKey key = selectionKeyIterator.next();

                if(key.isAcceptable()){
                    //新连接事件 为连接请求创建一个channel
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    System.out.println("客户端连接成功，生成一个channel：" + socketChannel.hashCode());

                    socketChannel.configureBlocking(false);

                    //将socketChannel 注册到selector, 关注事件为 OP_READ， 同时给socketChannel关联一个buffer
                    socketChannel.register(selector,SelectionKey.OP_READ, ByteBuffer.allocate(1024));

                }

                if(key.isReadable()){

                    //读事件
                    //读取对应channel的中buffer的中的数据

                    SocketChannel channel = (SocketChannel) key.channel();

                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    channel.read(buffer);

                    System.out.println("form 客户端 " + new String(buffer.array()));


                }


                selectionKeyIterator.remove();

            }


        }



    }
}