package at.review;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @create 2022-05-05
 */
public class NIOChannelCopy {


    public static void copyTwo() throws Exception {

        /*

            file1 -> inputChannel -> outputChannel -> file2

         */

        FileInputStream inputStream = new FileInputStream("files//1.txt");
        FileChannel inputChannel = inputStream.getChannel();

        FileOutputStream outputStream = new FileOutputStream("files//2.txt");
        FileChannel outputChannel = outputStream.getChannel();

        outputChannel.transferFrom(inputChannel, 0, inputChannel.size());

        inputChannel.close();
        inputStream.close();

        outputChannel.close();
        outputStream.close();


    }


    public static void copyOne() throws Exception {

        /*

            file1 -> inputChannel -> buffer -> outputBuffer -> file2

         */

        FileInputStream inputStream = new FileInputStream("files//1.txt");
        FileChannel inputChannel = inputStream.getChannel();

        FileOutputStream outputStream = new FileOutputStream("files//2.txt");
        FileChannel outputChannel = outputStream.getChannel();


        ByteBuffer buffer = ByteBuffer.allocate(32);


        while (true) {


            // !!!! 清空buffer
            buffer.clear();

            // 从 inputChannel 读取数据放入 buffer
            int read = inputChannel.read(buffer);

            System.out.println(read);

            if (read == -1) break;

            // ！！！ buffer 读写反转
            buffer.flip();

            // 将 buffer 中的数据写入 outputChannel
            outputChannel.write(buffer);


        }

        inputChannel.close();
        inputStream.close();

        outputChannel.close();
        outputStream.close();


    }

}
