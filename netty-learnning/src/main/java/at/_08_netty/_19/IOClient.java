package at._08_netty._19;


import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * @create 2023-05-28
 */
public class IOClient {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8090;
    private static final int SLEEP_TIME = 5;

    public static void main(String[] args) throws Exception {

        Socket socket = new Socket(HOST, PORT);

        new Thread(new Runnable() {
            @Override
            public void run() {

                System.out.println("客户端启动成功...");

                while (true) {

                    try {
                        String message = "hello world";
                        System.out.println("客户端发送的数据： " + message);
                        socket.getOutputStream().write(message.getBytes(StandardCharsets.UTF_8));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        TimeUnit.SECONDS.sleep(SLEEP_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }


            }
        }).start();

    }

}
