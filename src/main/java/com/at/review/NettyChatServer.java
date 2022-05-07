package com.at.review;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @create 2022-05-07
 */
public class NettyChatServer {

    public static final String HOST = "127.0.0.1";
    public static final int PORT = 8090;

    public NettyChatServer() {
    }

    public void run() {

        EventLoopGroup boosGroup = null;
        EventLoopGroup workGroup = null;


        try {
            boosGroup = new NioEventLoopGroup(1);
            workGroup = new NioEventLoopGroup(4);

            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .group(boosGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 64)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast("decode",new StringDecoder());
                            pipeline.addLast("encode",new StringEncoder());

                            pipeline.addLast("selfHandler",new NettyChatServerHandler());

                        }
                    });

            System.out.println("server initialize ok...");

            ChannelFuture channelFuture = serverBootstrap.bind(HOST, PORT).sync();

            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(future.isSuccess()) System.out.println("server 监听 8090 端口成功...");
                    else System.out.println("server 监听 8090 端口失败...");
                }
            });

            channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) {

        new NettyChatServer().run();

    }


}
