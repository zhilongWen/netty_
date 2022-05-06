package com.at.review;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @create 2022-05-06
 */
public class NIOZeroCopyServer {

    public static void main(String[] args) throws Exception{

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.socket().bind(new InetSocketAddress("127.0.0.1",8090));

        ByteBuffer buffer = ByteBuffer.allocate(8 * 1024);

        while (true){

            SocketChannel socketChannel = serverSocketChannel.accept();

            int read = 0;

            while ((read = socketChannel.read(buffer)) != -1){

                buffer.rewind();

            }
        }
    }
}
