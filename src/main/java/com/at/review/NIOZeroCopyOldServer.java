package com.at.review;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @create 2022-05-06
 */
public class NIOZeroCopyOldServer {

    public static void main(String[] args) throws Exception {


        ServerSocket serverSocket = new ServerSocket(8090);

        while (true) {

            Socket socket = serverSocket.accept();
            InputStream inputStream = socket.getInputStream();

            byte[] buffer = new byte[8 * 1024];

            while ((inputStream.read(buffer, 0, buffer.length)) != -1) {

            }
        }

    }
}
