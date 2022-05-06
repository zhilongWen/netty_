package com.at._02_nio._07_zero_copy;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @create 2022-04-19
 */
public class OldClient {

    //传统 io

    public static void main(String[] args) {

        Socket socket = null;
        FileInputStream inputStream = null;
        DataOutputStream outputStream = null;

        try {
            socket  = new Socket("127.0.0.1", 8090);

//            inputStream = new FileInputStream("files/protoc-3.6.1-win32.zip");
            inputStream = new FileInputStream("files/Typora.zip"); //77662345 字节

            outputStream  = new DataOutputStream(socket.getOutputStream());

            byte[] bytes = new byte[8 * 1024];
            long readCount;
            long total = 0;

            long startTime = System.currentTimeMillis();

            while ((readCount = inputStream.read(bytes)) != -1 ){
                total+=readCount;
                outputStream.write(bytes);
            }

            System.out.println("发送总字节数： " + total + ", 耗时： " + (System.currentTimeMillis() - startTime));



        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                outputStream.close();
                inputStream.close();
                socket.close();
            }catch (IOException e1){
                e1.printStackTrace();
            }
        }


    }

}
