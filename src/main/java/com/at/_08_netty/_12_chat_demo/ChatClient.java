package com.at._08_netty._12_chat_demo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

/**
 * @create 2022-04-25
 */
public class ChatClient {

    private String host;
    private int port;

    public ChatClient(String host, int port) {
        this.port = port;
        this.host = host;

    }

    public void run() {

        EventLoopGroup group = null;

        try {

            group = new NioEventLoopGroup(1);

            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            System.out.println("ChatClient ChannelInitializer");

                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast("encoder", new StringEncoder())
                                    .addLast("decoder", new StringDecoder());

                            pipeline.addLast(new ChatClientHandler());

                        }
                    });

            System.out.println("client initialize ok...");

            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();


            Channel channel = channelFuture.channel();
            System.out.println("client address channel:" + channel.localAddress());

            Scanner scanner = new Scanner(System.in);

            while (scanner.hasNextLine()) {

                String info = scanner.nextLine();

                //通过channel 发送到服务器端
                channel.writeAndFlush(info + "\r\n");

            }


        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }


    }


    public static void main(String[] args) {

        new ChatClient("127.0.0.1",8090).run();

    }

}


