package com.at._02_nio._03_filechannel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @create 2022-04-06
 */
public class _03_FileChannel_Copy {

    public static void main(String[] args) throws Exception{


        FileInputStream fileInputStream = new FileInputStream("files//1.txt");
        FileChannel fileInputChannel = fileInputStream.getChannel();


        FileOutputStream fileOutputStream = new FileOutputStream("files//2.txt");
        FileChannel fileOutChannel = fileOutputStream.getChannel();


        ByteBuffer byteBuffer = ByteBuffer.allocate(512);


        while (true){

            ////清空buffer
            byteBuffer.clear();

            int read = fileInputChannel.read(byteBuffer);

            System.out.println(read);

            if(read == -1) break;


            byteBuffer.flip();

            fileOutChannel.write(byteBuffer);


        }


        fileInputStream.close();
        fileOutputStream.close();


    }



}
