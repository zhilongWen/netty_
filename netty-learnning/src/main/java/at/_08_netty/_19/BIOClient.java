package at._08_netty._19;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * @create 2023-05-28
 */
public class BIOClient {


    public static final String HOST = "127.0.0.1";
    public static final int PORT = 8090;
    private Selector selector;
    private SocketChannel socketChannel;

    public BIOClient() {

        try {

            selector = Selector.open();
            socketChannel = SocketChannel.open(new InetSocketAddress(HOST, PORT));

            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {

        BIOClient client = new BIOClient();

        while (true) {

            try {
                client.socketChannel.write(ByteBuffer.wrap("hello BIO".getBytes(StandardCharsets.UTF_8)));
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

}
