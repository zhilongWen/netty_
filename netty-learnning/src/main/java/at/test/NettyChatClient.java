package at.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.Scanner;

/**
 * @create 2023-06-07
 */
public class NettyChatClient {

    public static void main(String[] args) {

        NioEventLoopGroup group = null;

        try {

            group = new NioEventLoopGroup();

            Bootstrap bootstrap = new Bootstrap();

            bootstrap
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .option(ChannelOption.SO_BACKLOG,8)
                    .option(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT)
                    .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ClientSelfChannelInitializer());

            Channel channel = bootstrap.connect("127.0.0.1", 9099).sync().channel();


            // 发送数据
            Scanner scanner = new Scanner(System.in);

            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                //通过channel 发送到服务器端
                channel.writeAndFlush(data + "\n");
            }

            // --------------------------------------------------------------------------------------


            channel.closeFuture().sync();


        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }

    }

}

class ClientSelfChannelInitializer extends ChannelInitializer<SocketChannel>{

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ch.pipeline()
                .addLast(new StringEncoder())
                .addLast(new StringDecoder())
                .addLast(
                        new SimpleChannelInboundHandler<String>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                System.out.println("client NettyChatClientHandler 读取到消息：" + msg + "\n");
                            }
                        }
                );

    }
}
