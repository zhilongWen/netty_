package com.at._01_bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @create 2022-04-03
 */
public class _02_BIOServer_accept_blocking {

    /*
        telnet 127.0.0.1 5590

        ctrl + ]

     */

    public static void main(String[] args) throws Exception{

        ExecutorService pool = Executors.newCachedThreadPool();


        //创建ServerSocket
        ServerSocket serverSocket = new ServerSocket(5590);


        System.out.println("服务启动了...");

        while (true){

            //BIO 线程池 位每个client创建一个 thread

            System.out.println("01_线程id：" + Thread.currentThread().getId() + " 线程名字：" + Thread.currentThread().getName());


            System.out.println("accept blocking test ......");
            Socket socket = serverSocket.accept();

            System.out.println("连接到一个客户端...");

            pool.execute(() -> {
                handler(socket);
            });

        }


    }


    public static void handler(Socket socket) {

        try {

            System.out.println("02_线程id：" + Thread.currentThread().getId() + " 线程名字：" + Thread.currentThread().getName());

            byte[] bytes = new byte[1024];

            InputStream inputStream = socket.getInputStream();

            while (true){

                System.out.println("03_线程id：" + Thread.currentThread().getId() + " 线程名字：" + Thread.currentThread().getName());

                int read = inputStream.read(bytes);

                if(read == -1) break;

                System.out.println(new String(bytes,0,read, Charset.defaultCharset()));

            }


        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            System.out.println("关闭连接...");
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
