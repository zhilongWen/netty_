package com.at.review;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @create 2022-05-05
 */
public class NIOMappedByteBuffer {

    public static void main(String[] args) throws Exception{


        RandomAccessFile randomAccessFile = new RandomAccessFile("files//1.txt","rw");

        FileChannel channel = randomAccessFile.getChannel();

        // 实际类型 DirectByteBuffer
        // 将 0 - 4 位置映射到内存中
        MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 5);

        mappedByteBuffer.put(0,(byte) 'Q');

        randomAccessFile.close();



    }

}
