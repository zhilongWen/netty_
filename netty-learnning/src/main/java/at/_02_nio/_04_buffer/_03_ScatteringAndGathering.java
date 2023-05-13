package at._02_nio._04_buffer;


import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * @create 2022-04-16
 */
public class _03_ScatteringAndGathering {


    /*

        Scattering：将数据写入到buffer时，可以采用buffer数组，依次写入  [分散]
        Gathering: 从buffer读取数据时，可以采用buffer数组，依次读


                    telnet 127.0.0.1
                        ↑
                        ↑   ServerSocket port 9098  read
                        ↑
          +-------------+   +-------------+   +-------------+
          | bytebuffer 0|   | bytebuffer 1|   | bytebuffer x|
          +-------------+   +-------------+   +-------------+
                                 ↑
                                 ↑ flip   read/write
                                 ↑



     */

    public static void main(String[] args) throws Exception {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress socketAddress = new InetSocketAddress(9098);

        //绑定端口到socket 并启动
        serverSocketChannel.socket().bind(socketAddress);


        //创建buffer
        ByteBuffer[] buffers = new ByteBuffer[2];
        buffers[0] = ByteBuffer.allocate(5);
        buffers[1] = ByteBuffer.allocate(3);

        SocketChannel socketChannel = serverSocketChannel.accept();

        int messageLen = 8;

        while (true){

            int readMsg = 0;

            while (readMsg < messageLen){

                long readL = socketChannel.read(buffers);
                readMsg+=readL;

                System.out.println("readMsg:" + readMsg);

                //使用流打印, 看看当前的这个buffer的position 和 limit
                Arrays.asList(buffers).stream()
                        .map(buffer -> "position:" + buffer.position()+"\t,limit:" + buffer.limit())
                        .forEach(System.out::println);

            }


            //将所有的buffer进行flip
            Arrays.stream(buffers).forEach(buffer -> buffer.flip());

            //将数据读出显示到客户端
            long writeMsg = 0;

            while (writeMsg < messageLen){
                long writeL = socketChannel.write(buffers);
                writeMsg += writeL;
            }

            //将所有的buffer 进行clear
            Arrays.stream(buffers).forEach(buffer -> buffer.clear());


            System.out.println("readMsg:" + readMsg + "\twriteMsg" + writeMsg + "\t messageLen:" + messageLen);




        }


    }

}
