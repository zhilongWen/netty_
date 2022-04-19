package com.at._02_nio._07_zero_copy;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/**
 * @create 2022-04-19
 */
public class ZeroCopyClient {

    public static void main(String[] args) throws Exception{

        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("127.0.0.1",8090));

        FileInputStream inputStream = new FileInputStream("files/protoc-3.6.1-win32.zip");

        FileChannel inputStreamChannel = inputStream.getChannel();

        long startTime = System.currentTimeMillis();

        //在linux下一个transferTo 方法就可以完成传输
        //在windows 下 一次调用 transferTo 只能发送8m , 就需要分段传输文件, 而且要主要
        //传输时的位置
        //transferTo 底层使用到零拷贝

        long transferCount = inputStreamChannel.transferTo(0, inputStreamChannel.size(), socketChannel);


        System.out.println("发送的总的字节数 =" + transferCount + " 耗时:" + (System.currentTimeMillis() - startTime));

        //关闭
        inputStreamChannel.close();


    }

}
