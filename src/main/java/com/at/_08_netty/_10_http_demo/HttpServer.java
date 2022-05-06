package com.at._08_netty._10_http_demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @create 2022-04-23
 *
 * 实例要求：
 *      Netty 服务器在 6668 端口监听，浏览器发出请求
 *      http://localhost:8090/
 *      服务器可以回复消息给客户端 "Hello! 我是服务器 .. " ,  并 对特定请求资源进行过滤.
 *
 *      目的：Netty 可以做Http服务开发，并且理解Handler实例 和客户端及其请求的关系.
 *
 *
 */
public class HttpServer {

    public static void main(String[] args) {

        EventLoopGroup boosGroup = null,workGroup = null;

        try {
            boosGroup = new NioEventLoopGroup(1);
            workGroup = new NioEventLoopGroup(2);

//            int i = 10/ 0;

            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boosGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,4)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .childHandler(new HttpChannelInitializer() );

            ChannelFuture channelFuture = serverBootstrap.bind(8090).sync();

            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(future.isSuccess()) System.out.println("server 端监听8090端口成功...");
                    else System.out.println("server 端监听8090端口失败...");
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

}
