package com.at._08_netty._11_buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * @create 2022-04-25
 */
public class NettyBufAndNioBuf {

    public static void main(String[] args) {

        nioBuf();


//        nettyBuf();

    }

    public static void nioBuf() {


        ByteBuffer buffer = ByteBuffer.allocate(5);


        for (int i = 0; i < buffer.capacity(); i++) {
            buffer.put((byte) i);
        }

        System.out.println("capacity：" + buffer.capacity());

        buffer.flip();

        for (int i = 0; i < buffer.capacity(); i++) {
            System.out.println(buffer.get());
        }


    }


    public static void nettyBuf() {

        /*

            1.ByteBuf 底层是一个 byte[]
            2.在 netty 的 ByteBuf 中无需使用 flip 反转
                其底层维护 readIndex 和 writeIndex
                通过 readIndex  writeIndex  capacity 将 buffer 分成三个区域

                0 --------- readIndex  已经读取的区域
                0 ------------------------ writeIndex 已写区域
                            readIndex ---- writeIndex 可读区域
                0 ----------------------------------------- capacity  容量
                                            writeIndex ---- capacity 可写区域


         */


        //创建一个buffer byte[5]
        ByteBuf buffer = Unpooled.buffer(5);

        for (int i = 0; i < buffer.capacity(); i++) {
            buffer.writeByte(i);
        }

        System.out.println("capacity:" + buffer.capacity());


        for (int i = 0; i < buffer.capacity(); i++) {
            System.out.println(buffer.getByte(i));
        }

        System.out.println("...........................................");

        for (int i = 0; i < buffer.capacity(); i++) {
            System.out.println(buffer.readByte());
        }


    }

}
