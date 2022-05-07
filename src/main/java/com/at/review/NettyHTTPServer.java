package com.at.review;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @create 2022-05-06
 */
public class NettyHTTPServer {

    /*
要求：
    Netty 服务器在 8090 端口监听，浏览器发出请求 http://localhost:8090/

    服务器可以回复消息给客户端 "Hello! i am server ... " ,  并 对特定请求资源进行过滤.

    目的：Netty 可以做Http服务开发，并且理解Handler实例 和客户端及其请求的关系.


     */

    public static void main(String[] args) {

        EventLoopGroup bossGroup = null,workGroup = null;


        try {
            bossGroup = new NioEventLoopGroup(1);
            workGroup = new NioEventLoopGroup(4);

            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            System.out.println("初始化server端channel对象...");

                            ChannelPipeline pipeline = ch.pipeline();

                             /*
                                加入一个netty 提供的httpServerCodec codec =>[coder - decoder]
                                    1. HttpServerCodec 是netty 提供的处理http的 编-解码器
                                    2. 增加一个自定义的handler
                            */

                            // decode encode
                            pipeline.addLast(new HttpServerCodec());

                            pipeline.addLast(new NettyHTTPServerHandler());


                        }
                    });

            ChannelFuture channelFuture = serverBootstrap.bind("127.0.0.1", 8090).sync();

            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }


    }

}
