package com.at._03_nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * @create 2022-04-06
 */
public class _05_Buffer_PutGet_Type {

    public static void main(String[] args) throws Exception{


        ByteBuffer buffer = ByteBuffer.allocate(24);

        buffer.put("dfvdf".getBytes(StandardCharsets.UTF_8));
        buffer.putInt(123);
        buffer.putChar('s');

        buffer.flip();


        System.out.println(buffer.getInt());
        System.out.println(buffer.getInt());
        System.out.println(buffer.getInt());


    }



}
