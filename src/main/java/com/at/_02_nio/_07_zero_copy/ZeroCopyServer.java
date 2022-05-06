package com.at._02_nio._07_zero_copy;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @create 2022-04-19
 */
public class ZeroCopyServer {

    public static void main(String[] args) throws Exception{

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        ServerSocket serverSocket = serverSocketChannel.socket();

        serverSocket.bind(new InetSocketAddress(8090));

        ByteBuffer buffer = ByteBuffer.allocate(8 * 1024);


        while (true){

            SocketChannel socketChannel = serverSocketChannel.accept();

            int readCount = 0;

            while (readCount != -1){

                try {
                    readCount = socketChannel.read(buffer);
                }catch (Exception e){
                    break;
                }

                //倒带 position = 0 mark 作废
                buffer.rewind();

            }

        }



    }

}
