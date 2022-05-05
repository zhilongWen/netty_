package com.at.review;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @create 2022-05-05
 */
public class NIOChannelRead {

    public static void main(String[] args) throws Exception {

        //file -> input stream -> channel -> buffer

        File file = new File("files//1.txt");

        FileInputStream fileInputStream = new FileInputStream(file);

        FileChannel channel = fileInputStream.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate((int) file.length());

        channel.read(buffer);

        System.out.println(new String(buffer.array()));

        channel.close();
        fileInputStream.close();


    }

}
