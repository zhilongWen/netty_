package com.at._02_nio._07_zero_copy;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @create 2022-04-19
 */
public class OldServer {

    //传统 io

    public static void main(String[] args) throws Exception {


        ServerSocket serverSocket = new ServerSocket(8090);


        while (true) {

            Socket socket = serverSocket.accept();
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());

            byte[] bytes = new byte[8 * 1024];

            while (true) {
                int read = inputStream.read(bytes, 0, bytes.length);

                if (read == -1) break;

            }

        }


    }

}
