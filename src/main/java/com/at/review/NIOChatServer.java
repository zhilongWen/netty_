package com.at.review;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

/**
 * @create 2022-05-05
 */
public class NIOChatServer {

    private final int PORT = 8090;
    private final String HOSTNAME = "127.0.0.1";
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;

    public NIOChatServer() {

        try {

            // 创建 selector
            selector = Selector.open();

            // 创建
            serverSocketChannel = ServerSocketChannel.open();
            // 绑定 port
            serverSocketChannel.socket().bind(new InetSocketAddress(HOSTNAME, PORT));
            // 设置非阻塞模式
            serverSocketChannel.configureBlocking(false);

            // 注册到 selector
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void listen() {

        try {

            while (true) {

                // 所有的 channel 无事件
                if (selector.select() == 0) {
//                    System.out.println("等待服务器连接.......");
                    continue;
                }

                // 获取有事件的 channel
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

                // 处理有事件发生的 channel
                while (keyIterator.hasNext()) {

                    SelectionKey key = keyIterator.next();

                    // accept 事件
                    if (key.isAcceptable()) handlerAccept(key);

                    // read 事件
                    if (key.isReadable()) handlerRead(key);

                    // 处理完成 移除 channel
                    keyIterator.remove();

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void handlerAccept(SelectionKey key) {

        try {

            System.out.println("处理 accept 事件 ......");

            SocketChannel channel = serverSocketChannel.accept();

            channel.configureBlocking(false);

            channel.register(selector, SelectionKey.OP_READ);

            System.out.println(channel.getRemoteAddress() + " \t 机器已上线");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void handlerRead(SelectionKey key) {

        System.out.println("处理 read 事件 ......");


        SocketChannel channel = null;

        try {

            channel = (SocketChannel) key.channel();

//            ByteBuffer buffer = (ByteBuffer) key.attachment(); // java.lang.NullPointerException
            ByteBuffer buffer = ByteBuffer.allocate(1024);


            int read = channel.read(buffer);

            if (read > 0) {

                String msg = new String(buffer.array(), Charset.forName("utf-8")).trim();

                System.out.println("收到来自 " + channel.getRemoteAddress() + " 的信息 msg = " + msg);

                //将数据转发给其他 client 端
                sendOtherClient(channel, msg);

            }

        } catch (IOException e) {

            try {

                System.out.println(channel.getRemoteAddress() + " 机器下线........");
                //取消注册 关闭通道
                key.cancel();
                channel.close();

            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }


    public void sendOtherClient(SocketChannel selfChannel, String msg) throws IOException {

        System.out.println(selfChannel.getRemoteAddress() + " 机器正在转发数据.................");

        for (SelectionKey key : selector.keys()) {


            SelectableChannel ch = key.channel();

            if (ch instanceof SocketChannel && ch != selfChannel) {

                SocketChannel socketChannel = (SocketChannel) ch;

                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8));

                socketChannel.write(buffer);

                System.out.println(selfChannel.getRemoteAddress() + " 机器 向 " + socketChannel.getRemoteAddress() + " 机器转发数据成功.......");

            }
        }
    }


    public static void main(String[] args) {

        System.out.println("server running..........");

        NIOChatServer server = new NIOChatServer();

        server.listen();

    }

}
