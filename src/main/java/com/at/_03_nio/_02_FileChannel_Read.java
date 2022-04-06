package com.at._03_nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * @create 2022-04-05
 */
public class _02_FileChannel_Read {


    public static void main(String[] args) throws Exception{


        //file -> input stream -> channel -> buffer


        File file = new File("files//1.txt");

        FileInputStream fileInputStream = new FileInputStream(file);

        FileChannel  fileChannel = fileInputStream.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate((int) file.length());


        int read = fileChannel.read(byteBuffer);

        System.out.println(new String(byteBuffer.array()));


        fileInputStream.close();


    }
}
