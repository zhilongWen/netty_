package com.at._02_nio;

import java.nio.IntBuffer;

/**
 * @create 2022-04-04
 */
public class NIOBuffer {

    /*

        nio 三大核心组件
            channel buffer selector
                                     thread
                                        ↓
                                    selector
                                    /     \
                                   /       \
                                channel    channel
                                 ↑↓           ↑↓ flip切换
                               buffer        buffer


        1）每个channel都会对应一个Buffer
        2）Selector对应一个线程，一个线程对应多个channel（连接）
        3）channel注册到selector
        4）程序切换到哪个channel是有事件决定的，Event就是一个重要的概念
        5）Selector会根据不同的事件，在各个通道上切换
        6）Buffer就是一个内存块，底层是有一个数组
        7）数据的读取写入是通过Buffer，这个和BIO，BIO中要么是输入流，或者是输出流，不能双向，
            但是NIO的Buffer是可以读也可以写，需要flip方法切换channel是双向的，可以返回底层操作系统的情况，比如Linux，底层的操作系统通道就是双向的


    private int mark = -1;   标记
    private int position = 0;  下一次要被读或写的位置 每次读写缓冲区数据时都会改变该值，为下一次读写做准备
    private int limit;    缓冲区的终点，不能对超过limit的位置进行读写操作，可以修改
    private int capacity;  容量 创建后不能修改


     */

    public static void main(String[] args) {

        //创建一个 buffer 大小位5
        IntBuffer buffer = IntBuffer.allocate(5);

//        for (int i = 0; i < buffer.capacity(); i++) {
        for (int i = 0; i < 6; i++) {
            buffer.put(i * 2);
        }

        //从buffer 中读取数据
        //在buffer中可以进行读写切换
        buffer.flip();

        while (buffer.hasRemaining()) {
            System.out.println(buffer.get());
        }


    }

}
