package at._08_netty._19;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * @create 2023-05-28
 */
public class BIOServer {

    public static void main(String[] args) {

        BIOServer bioServer = new BIOServer();
        bioServer.listen();

    }


    private Selector selector;
    private ServerSocketChannel serverSocketChannel;


    public BIOServer() {

        try {

            //获取selector
            selector = Selector.open();
            //获取channel
            serverSocketChannel = ServerSocketChannel.open();

            //绑定端口
            serverSocketChannel.bind(new InetSocketAddress(8090));
            //设置为非阻塞模式
            serverSocketChannel.configureBlocking(false);

            //绑定端口
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void listen() {

        try {

            while (true) {

                System.out.println("服务器监听线程：" + Thread.currentThread().getName());

                int select = selector.select();

                if (select < 1) {
                    System.out.println("服务器等待事件中...");
                    continue;
                }

                //获取所有触发的事件的channel
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();

                while (iterator.hasNext()) {

                    SelectionKey selectionKey = iterator.next();

                    if (selectionKey.isAcceptable()) {

                        System.out.println("监听到 accept 事件");

                        SocketChannel channel = serverSocketChannel.accept();
                        channel.configureBlocking(false);
                        //将channel注册到selector
                        channel.register(selector, SelectionKey.OP_READ);

                    }

                    if (selectionKey.isReadable()) {

                        System.out.println("监听到 read 事件");

                        //获取channel
                        SocketChannel channel = (SocketChannel) selectionKey.channel();
                        // 创建 buffer
                        ByteBuffer buffer = ByteBuffer.allocate(1024);

                        //从channel读取数据到buffer中
                        int read = channel.read(buffer);


                        if (read > 0) {

                            String msg = new String(buffer.array(), StandardCharsets.UTF_8);

                            System.out.println("from 客户端信息：" + msg);

                        }


                    }


                    iterator.remove();

                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
