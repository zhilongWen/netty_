package com.at.review;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @create 2022-05-04
 */
public class BIO {


    public static void main(String[] args) throws Exception {


        ServerSocket serverSocket = new ServerSocket(8090);

        while (true) {

            Socket socket = serverSocket.accept();

            System.out.println("连接到一个客户端...");

            new Thread(() -> handler(socket)).start();

        }

    }

    public static void handler(Socket socket) {
        try {

            byte[] buffer = new byte[1024];

            InputStream inputStream = socket.getInputStream();
            while (true) {

                int read = inputStream.read(buffer);

                if (read == -1) break;

                System.out.println(new String(buffer, 0, read, Charset.forName("utf-8")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
