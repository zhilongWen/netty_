package com.at.rpc.bio.client;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;

public class BioClient {

    public static void main(String[] args) {

        try {

            Socket socket = new Socket("192.168.43.159", 9090);
            System.out.println("client start...");

            BioClientHandler handler = new BioClientHandler(socket, Charset.forName("utf-8"));
            handler.start();

        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
