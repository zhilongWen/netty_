package at._02_nio._04_buffer;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

/**
 * @create 2022-04-16
 */
public class _02_MappedByteBuffer {

    /*
        MappedByteBuffer 直接在内存(堆内存)中修改文件,无需操作系统拷贝一次
     */

    public static void main(String[] args)throws Exception {


        RandomAccessFile randomAccessFile = new RandomAccessFile("files//1.txt", "rw");


        FileChannel channel = randomAccessFile.getChannel();


        // 实际类型 DirectByteBuffer
        MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 5);

//        mappedByteBuffer.put("JAVA".getBytes(Charset.defaultCharset()));   //从起始位置进行修改
//        mappedByteBuffer.put("大数据Hadoop".getBytes(Charset.defaultCharset()));  //BufferOverflowException


        mappedByteBuffer.put(0, (byte) 'H');
        mappedByteBuffer.put(5, (byte) 'H');


        randomAccessFile.close();


    }

}
