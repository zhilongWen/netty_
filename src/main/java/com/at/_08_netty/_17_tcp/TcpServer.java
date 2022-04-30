package com.at._08_netty._17_tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @create 2022-04-30
 */
public class TcpServer {

    public static void main(String[] args) throws Exception{

        EventLoopGroup boosGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup(4);


        try {

            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .group(boosGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 32)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new TcpServerInitializer());

            System.out.println("server 端 initialise ok...");

            ChannelFuture channelFuture = serverBootstrap.bind(8090).sync();

            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(future.isSuccess()) System.out.println("server 监听 8090 端口 成功..");
                    else System.out.println("server 监听 8090 端口 失败...");
                }
            });

            channelFuture.channel().closeFuture().sync();

        } finally {

            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();

        }


    }

}
