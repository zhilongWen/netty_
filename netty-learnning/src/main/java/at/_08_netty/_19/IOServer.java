package at._08_netty._19;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @create 2023-05-28
 */
public class IOServer {

    public static void main(String[] args) {

        IOServer server = new IOServer(8090);

        server.start0();

    }


    private ServerSocket serverSocket;

    public IOServer(int port) {

        try {
            this.serverSocket = new ServerSocket(port);
            System.out.println("服务端启动成功，port = " + port);
        } catch (IOException e) {
            System.out.println("服务端启动失败...");
        }

    }


    public void start0() {
        // 单独创建一个线程不阻塞主线程
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {

                    try {
                        // 堵塞处理 accept 请求
                        Socket socket = serverSocket.accept();

                        handlerReadWrite(socket);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    public void handlerReadWrite(Socket socket) {

        System.out.println("服务端处理读写请求");

        // 为每个客户端创建一个线程，不阻塞 accept
        new Thread(() -> {
            try {
                InputStream inputStream = socket.getInputStream();

                while (true) {

                    byte[] data = new byte[1024];
                    int len;
                    while ((len = inputStream.read(data)) != -1) {

                        String message = new String(data, 0, len);

                        System.out.println("服务端接收到的客户端信息：" + message);

                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();


    }


}
