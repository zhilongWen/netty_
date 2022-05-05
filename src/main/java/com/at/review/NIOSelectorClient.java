package com.at.review;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * @create 2022-05-05
 */
public class NIOSelectorClient {


    public static void main(String[] args) throws Exception{

        SocketChannel socketChannel = SocketChannel.open();

        socketChannel.configureBlocking(false);

        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 8090);

        if (!socketChannel.connect(inetSocketAddress)){
            while (!socketChannel.finishConnect()){
                System.out.println("client端正在连接server端....");
                try { TimeUnit.SECONDS.sleep(3); } catch (InterruptedException e) { e.printStackTrace(); }
            }
        }


        System.out.println("client 连接 server 端成功.....");


        String data = "hello，netty，搭建";

        ByteBuffer buffer = ByteBuffer.wrap(data.getBytes(StandardCharsets.UTF_8));

        // 讲数据写入 buffer 发送到 socket
        socketChannel.write(buffer);

        socketChannel.close();

        System.in.read();


    }

}
