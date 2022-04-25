package com.at._08_netty._12_chat_demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @create 2022-04-25
 */
public class ChatServer {

    private int port;


    public ChatServer(int port) {
        this.port = port;

    }

    public void run() {

        EventLoopGroup boosGroup = null, workGroup = null;

        try {

            boosGroup = new NioEventLoopGroup(1);
            workGroup = new NioEventLoopGroup(4);

            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .group(boosGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 64)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
//                    .handler(null)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            System.out.println("server ChannelInitializer...");

                            ChannelPipeline pipeline = ch.pipeline();

                            //向pipeline加入解码器 编码器
                            pipeline.addLast("decoder",new StringDecoder());
                            pipeline.addLast("encoder",new StringEncoder());

                            //加入自己的业务处理handler
                            pipeline.addLast(new ChatServerHandler());



                        }
                    });

            System.out.println("server initialize ok...");

            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();

            //监听关闭
            channelFuture.channel().closeFuture().sync();


        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }


    }




    public static void main(String[] args) {

        new ChatServer(8090).run();

    }

}
