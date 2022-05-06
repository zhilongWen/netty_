package com.at._02_nio._06_chat_demo;


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
 * @create 2022-04-19
 */
public class ChatClient {

    public static final String HOST = "127.0.0.1";
    public static final int PORT = 8090;
    private Selector selector;
    private SocketChannel socketChannel;
    private String username;


    public ChatClient() {

        try {

            selector = Selector.open();
            socketChannel = SocketChannel.open(new InetSocketAddress(HOST, PORT));

            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);

            username = socketChannel.getLocalAddress().toString().substring(1);

            System.out.println(username + " is ok...");

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    /**
     * 向服务器发送数据
     * @param info
     */
    public void sendInfo(String info){

        info = info + " 说：" + info;

        try {
            socketChannel.write(ByteBuffer.wrap(info.getBytes(StandardCharsets.UTF_8)));
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public void readInfo(){

        try {

            int readChannels = selector.select();

            if(readChannels > 0){
                //有可用通道

                Iterator<SelectionKey> selectionKeyIterator = selector.selectedKeys().iterator();

                while (selectionKeyIterator.hasNext()){
                    SelectionKey selectionKey = selectionKeyIterator.next();

                    if(selectionKey.isReadable()){

                        SocketChannel channel = (SocketChannel) selectionKey.channel();

                        ByteBuffer buffer = ByteBuffer.allocate(1024);

                        channel.read(buffer);

                        String msg = new String(buffer.array());

                        System.out.println("client read info : " + msg.trim());
                    }

                }

                selectionKeyIterator.remove();

            }else {
//                System.out.println("还没有可用通道");
            }

        }catch (IOException e){
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {

        System.out.println("client running..........");


        ChatClient chatClient = new ChatClient();

        //启动一个线程，每隔3s 从服务器读取数据
        new Thread(() -> {

            while (true){
                chatClient.readInfo();
                try { TimeUnit.SECONDS.sleep(3); } catch (InterruptedException e) { e.printStackTrace(); }
            }

        }).start();

        //发送数据给客户端
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()){
            chatClient.sendInfo(scanner.nextLine());
        }


    }


}
