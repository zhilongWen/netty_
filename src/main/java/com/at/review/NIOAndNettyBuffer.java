package com.at.review;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @create 2022-05-10
 */
public class NIOAndNettyBuffer {


    public static void main(String[] args) {

        ByteBuf buffer = Unpooled.copiedBuffer("qweqweqw".getBytes(StandardCharsets.UTF_8));


    }


    public static void main1(String[] args) {

        ByteBuffer buffer = ByteBuffer.allocate(5);

        buffer.put((byte) 1);
        buffer.put((byte) 2);
        buffer.put((byte) 3);
        buffer.put((byte) 'A');
        buffer.put((byte) 'H');

        System.out.println("capacity：" + buffer.capacity());


        buffer.flip();

        for (int i = 0; i < buffer.capacity(); i++) {
            System.out.println(buffer.get());
        }

        ByteBuffer byteBuffer = ByteBuffer.wrap("asdsadsa".getBytes(StandardCharsets.UTF_8));

    }

}
