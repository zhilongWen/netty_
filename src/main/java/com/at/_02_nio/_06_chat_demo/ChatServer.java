package com.at._02_nio._06_chat_demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

/**
 * @create 2022-04-19
 */
public class ChatServer {


    private Selector selector;
    private ServerSocketChannel listenChannel;
    public static final int PORT = 8090;

    public ChatServer() {

        try {
            //获取selector
            selector = Selector.open();
            //获取channel
            listenChannel = ServerSocketChannel.open();
            //绑定端口
            listenChannel.bind(new InetSocketAddress(PORT));
            //设置为非阻塞模式
            listenChannel.configureBlocking(false);

            //将channel注册到selector
            listenChannel.register(selector, SelectionKey.OP_ACCEPT);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 监听事件
     */
    public void listen() {


        try {

            while (true) {

                System.out.println("服务器监听线程：" + Thread.currentThread().getName());

                int count = selector.select();

                if (count < 1) {
                    System.out.println("服务器等待事件中...");
                    continue;
                }

                //获取所有触发的事件的channel
                Iterator<SelectionKey> selectionKeyIterator = selector.selectedKeys().iterator();

                while (selectionKeyIterator.hasNext()) {

                    SelectionKey selectionKey = selectionKeyIterator.next();

                    //监听 accept 事件
                    if (selectionKey.isAcceptable()) {

                        System.out.println("监听到 accept 事件");

                        SocketChannel channel = listenChannel.accept();
                        channel.configureBlocking(false);
                        //将channel注册到selector
                        channel.register(selector, SelectionKey.OP_READ);
                        System.out.println(channel.getRemoteAddress() + "\t 机器已上线");

                    }

                    //read事件
                    if (selectionKey.isReadable()) {

                        System.out.println("监听到 read 事件");
                        readData(selectionKey);

                    }

                    //当前的key 删除，防止重复处理
                    selectionKeyIterator.remove();


                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 从指定的channel中读取数据
     *
     * @param selectionKey
     */
    public void readData(SelectionKey selectionKey) {

        SocketChannel channel = null;

        try {

            System.out.println("服务器read线程：" + Thread.currentThread().getName());

            //获取channel
            channel = (SocketChannel) selectionKey.channel();

            //创建buffer
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            //从channel读取数据到buffer中
            int readMsg = channel.read(buffer);

            if (readMsg > 0) {
                //将缓存的数据转为字符串
                String msg = new String(buffer.array());

                System.out.println("from 客户端信息：" + msg);

                //将消息转发到其他客户端
                sendInfoToOtherClients(msg,channel);


            }

        } catch (IOException e) {

            try {
                System.out.println(channel.getRemoteAddress() + "\t client 下线了...");
                //取消注册 关闭通道
                selectionKey.cancel();
                channel.close();
            }catch (IOException e1){
                e1.printStackTrace();
            }

        }

    }


    /**
     * 将消息转发到其他客户端，并排除自己
     *
     * @param info
     * @param selfChannel
     */
    public void sendInfoToOtherClients(String info, SocketChannel selfChannel) throws IOException {

        System.out.println(selfChannel.getRemoteAddress() + " \t服务器正在转发数据....");

        System.out.println("服务器send线程：" + Thread.currentThread().getName());

        //获取selector中的所有channel
        for (SelectionKey key : selector.keys()) {

            //获取到各个channel
            SelectableChannel channel = key.channel();

            //判断是否为socketChannel 并排除自身
            if (channel instanceof SocketChannel && channel != selfChannel) {

                System.out.println("将数据转发给了：" + ((SocketChannel) channel).getRemoteAddress() + " 服务器");

                SocketChannel descChannel = (SocketChannel) channel;
                ByteBuffer buffer = ByteBuffer.wrap(info.getBytes(StandardCharsets.UTF_8));

                descChannel.write(buffer);

            }

        }


    }


    public static void main(String[] args) {


        System.out.println("server running..........");

        ChatServer chatServer = new ChatServer();

        chatServer.listen();

    }


}
