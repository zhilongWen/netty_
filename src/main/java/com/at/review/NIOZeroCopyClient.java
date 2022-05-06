package com.at.review;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/**
 * @create 2022-05-06
 */
public class NIOZeroCopyClient {

    public static void main(String[] args) throws Exception {

        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 8090));

        File file = new File("files//Typora.zip");

        long fileSize = file.length();

        System.out.println("文件大小 fileSize = " + fileSize);

        FileInputStream inputStream = new FileInputStream(file);
        FileChannel inputStreamChannel = inputStream.getChannel();

        //在linux下一个transferTo 方法就可以完成传输
        //在windows 下 一次调用 transferTo 只能发送 8m , 就需要分段传输文件, 而且要主要
        //传输时的位置 =》 课后思考...
        //transferTo 底层使用到零拷贝

        long count = (long) ((fileSize % (8 * 1024 * 1024)) == 0 ? fileSize / (8 * 1024 * 1024) : fileSize / (8 * 1024 * 1024) + 1);

        int position = 0;

        long startTime = System.currentTimeMillis();

        while (count-- > 0) {

            long transferCount = inputStreamChannel.transferTo(position, 8 * 1024 * 1024, socketChannel);

            System.out.println("发送的总的字节数 =" + transferCount + " 耗时:" + (System.currentTimeMillis() - startTime));

            position += (8 * 1024 * 1024);
        }

        //关闭
        inputStreamChannel.close();

    }

}
