package com.at.review;


import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * @create 2022-05-05
 */
public class NIOScatteringAndGathering {
    /*

        Scattering：将数据写入到buffer时，可以采用buffer数组，依次写入  [分散]
        Gathering: 从buffer读取数据时，可以采用buffer数组，依次读


                    telnet 127.0.0.1
                        ↑
                        ↑   ServerSocket port 9098  read
                        ↑
          +-------------+   +-------------+   +-------------+
          | bytebuffer 0|   | bytebuffer 1|   | bytebuffer x|
          +-------------+   +-------------+   +-------------+
                                 ↑
                                 ↑ flip   read/write
                                 ↑



     */

    public static void main(String[] args) throws Exception {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        // 绑定端口到socket 并启动
        serverSocketChannel.socket().bind(new InetSocketAddress(8090));

        // 创建buffer
        ByteBuffer[] buffers = new ByteBuffer[2];
        buffers[0] = ByteBuffer.allocate(3);
        buffers[1] = ByteBuffer.allocate(2);

        SocketChannel socketChannel = serverSocketChannel.accept();

        int messageLen = 5;

        while (true) {

            int readLen = 0;

            while (readLen < messageLen) {

                long read = socketChannel.read(buffers);

                readLen += read;

                System.out.println("readLen = " + readLen);

            }

            // 将所有的buffer进行flip
            Arrays.stream(buffers).map(b -> b.flip());

            long writeLen = 0;

            while (writeLen < messageLen) {

                writeLen += socketChannel.write(buffers);

            }

            //将所有的buffer 进行clear
            Arrays.stream(buffers).map(b -> b.clear());


        }


    }

}
