package com.at.review;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @create 2022-05-06
 */
public class NIOZeroCopyOldClient {

    public static void main(String[] args) {

        Socket socket = null;
        FileInputStream inputStream = null;
        DataOutputStream outputStream = null;

        try {

            socket = new Socket("127.0.0.1", 8090);
            outputStream = new DataOutputStream(socket.getOutputStream());

            inputStream = new FileInputStream(new File("files//Typora.zip"));

            byte[] buffer = new byte[8 * 1024];

            long read;
            long total = 0; //统计文件字节数

            long startTime = System.currentTimeMillis();

            while ((read = inputStream.read(buffer)) != -1) {

                total += read;
                outputStream.write(buffer);

            }

            System.out.println("发送总字节数： " + total + ", 耗时： " + (System.currentTimeMillis() - startTime));

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                outputStream.close();
                inputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
