package com.at._02_nio._03_filechannel;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * @create 2022-04-05
 */
public class _01_FileChannel_Write {


    /*

        NIO的channel类似于流，但有些区别如下：
            channel可以同时进行读写，而流只能读或者只能写
            channel可以实现异步读写数据
            channel可以从缓冲读数据，也可以写数据到缓冲

            channel ←→ buffer


        常用的 Channel 类有：
            FileChannel、
            DatagramChannel、
            ServerSocketChannel    类似 ServerSocket
            SocketChannel 类似 Socket


        FileChannel 用于文件的数据读写，
        DatagramChannel 用于 UDP 的数据读写，
        ServerSocketChannel 和 SocketChannel 用于 TCP 的数据读写



        FileChannel 类

        FileChannel主要用来对本地文件进行 IO 操作，常见的方法有

        public int read(ByteBuffer dst) ，从通道读取数据并放到缓冲区中
        public int write(ByteBuffer src) ，把缓冲区的数据写到通道中
        public long transferFrom(ReadableByteChannel src, long position, long count)，从目标通道 中复制数据到当前通道
        public long transferTo(long position, long count, WritableByteChannel target)，把数据从当 前通道复制给目标通道







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

    public static void main(String[] args) throws Exception{


        String str = "hello,netty";

        //创建一个输出流->channel
        FileOutputStream fileOutputStream = new FileOutputStream("D:\\workspace\\netty_\\files\\1.txt");

        //通过 fileOutputStream 获取 对应的 FileChannel
        //这个 fileChannel 真实 类型是  FileChannelImpl
        FileChannel fileChannel = fileOutputStream.getChannel();

        //创建一个缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        byteBuffer.put(str.getBytes(StandardCharsets.UTF_8));

        byteBuffer.flip();

        //将数据写入channel
        fileChannel.write(byteBuffer);

        fileOutputStream.close();


    }

}
