package com.at.review;

import java.nio.Buffer;
import java.nio.IntBuffer;
import java.util.Arrays;

/**
 * @create 2022-05-04
 */
public class NIOBuffer {

    public static void main(String[] args) {

        // 创建一块 int 类型 大小为 3 的缓冲区
        IntBuffer buffer = IntBuffer.allocate(5);



        // 向 buffer 中写入数据
        buffer.put(1);
        buffer.put(2);
        buffer.put(3);
        buffer.put(4);
        buffer.put(5);


        // buffer 读写切换
        buffer.flip();

        buffer.limit(4);
        buffer.position(2);

        // 从buffer 中读取数据
        while (buffer.hasRemaining()) {
            System.out.println(buffer.get());
        }

    }

}
