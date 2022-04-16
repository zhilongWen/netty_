package com.at._02_nio._04_buffer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @create 2022-04-06
 */
public class _01_Buffer_PutGet_Type {

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
