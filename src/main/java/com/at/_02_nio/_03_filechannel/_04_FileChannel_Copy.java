package com.at._02_nio._03_filechannel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * @create 2022-04-06
 */
public class _04_FileChannel_Copy {

    public static void main(String[] args) throws Exception{

        FileInputStream fileInputStream = new FileInputStream("files//1.png");
        FileChannel inputStreamChannel = fileInputStream.getChannel();


        FileOutputStream fileOutputStream = new FileOutputStream("files//2.png");
        FileChannel outputStreamChannel = fileOutputStream.getChannel();

        //使用transferForm完成拷贝
        outputStreamChannel.transferFrom(inputStreamChannel,0,inputStreamChannel.size());




        inputStreamChannel.close();
        fileInputStream.close();

        outputStreamChannel.close();
        fileOutputStream.close();


    }



}
