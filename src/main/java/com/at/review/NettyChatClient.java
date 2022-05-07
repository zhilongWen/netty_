package com.at.review;

import io.netty.bootstrap.Bootstrap;
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
 * @create 2022-05-07
 */
public class NettyChatClient {

    public void run() {

        EventLoopGroup group = null;

        try {
            group = new NioEventLoopGroup(1);

            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ch.pipeline().addLast(new StringDecoder())
                                    .addLast(new StringEncoder())
                                    .addLast(new SimpleChannelInboundHandler<String>() {
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                            System.out.println("client NettyChatClientHandler 读取到消息：" + msg + "\n");
                                        }
                                    });
                        }
                    });


            System.out.println("client initialize ok...");

            ChannelFuture channelFuture = bootstrap.connect(NettyChatServer.HOST, NettyChatServer.PORT).sync();

            // --------------------------------------------------------------------------------------

            // 发送数据
            Channel channel = channelFuture.channel();

            Scanner scanner = new Scanner(System.in);

            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                //通过channel 发送到服务器端
                channel.writeAndFlush(data + "\n");
            }

            // --------------------------------------------------------------------------------------

            channelFuture.channel().closeFuture().sync();


        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }

    }

    public static void main(String[] args) {

        new NettyChatClient().run();

    }


}
