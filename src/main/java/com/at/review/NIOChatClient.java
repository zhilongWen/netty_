package com.at.review;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * @create 2022-05-05
 */
public class NIOChatClient {


    private final int PORT = 8090;
    private final String HOSTNAME = "127.0.0.1";
    private SocketChannel socketChannel;
    private Selector selector;


    public NIOChatClient() {

        try {

            selector = Selector.open();

            socketChannel = SocketChannel.open(new InetSocketAddress(HOSTNAME, PORT));
            socketChannel.configureBlocking(false);

            // 只关心读事件
            socketChannel.register(selector, SelectionKey.OP_READ);

            System.out.println(socketChannel.getLocalAddress() + " id ok............");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void handlerRead() {

        try {

//            if (selector.select() == 0) return;

            int eventChannels = selector.select();

            if (eventChannels > 0) {

                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

                while (keyIterator.hasNext()) {

                    SelectionKey key = keyIterator.next();

                    if (key.isReadable()) {

                        SocketChannel channel = (SocketChannel) key.channel();

                        ByteBuffer buffer = ByteBuffer.allocate(1024);

                        channel.read(buffer);

                        String data = new String(buffer.array());

                        System.out.println("client read data : " + data.trim());

                    }

                    // 移除处理完的 channel
                    keyIterator.remove();

                }

            } else {

            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void send(String msg) {

        try {

            msg = socketChannel.getLocalAddress() + " 发出的数据 " + msg;

            socketChannel.write(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {

        System.out.println("client running..........");

        NIOChatClient client = new NIOChatClient();

        //启动一个线程，每隔3s 从服务器读取数据
        new Thread(() -> {

            while (true) {
                client.handlerRead();
                try { TimeUnit.SECONDS.sleep(3); } catch (InterruptedException e) { e.printStackTrace(); }
            }

        }).start();


        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            client.send(scanner.nextLine());
        }

    }

}
