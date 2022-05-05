package com.at.review;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * @create 2022-05-05
 */
public class NIOChannel {

    /*

    将 data 数据 写入 file 中

          data
            |
            |
            |
            ↓
       ---------------         -------------------------------
       | ByteBuffer  |         | FileChannel                 |
       |             |  -----> |          ------------------ | ----> file
       |   data      |         |          | java输入/出流对象| |
       ---------------          ------------------------------


     */
    public static void main(String[] args) throws Exception {


        String data = "hello,netty 哈哈";

        // 创建一个输出流
        FileOutputStream fileOutputStream = new FileOutputStream("files//1.txt");

        // 通过输出流获取对应的 channel ， fileChannel 的实际类型是  FileChannelImpl
        FileChannel fileChannel = fileOutputStream.getChannel();

        // 创建一个 buffer
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        // 将数据放入 buffer
        buffer.put(data.getBytes(StandardCharsets.UTF_8));

        // buffer 读写反转
        buffer.flip();

        // 将数据写入 channel
        fileChannel.write(buffer);

        // 关闭通道
        fileChannel.close();
        fileOutputStream.close();


    }

}
