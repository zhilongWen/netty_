package com.at._08_netty._13_heart_beat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @create 2022-04-25
 */
public class HeartBeatServer {

    public static void main(String[] args) {

        EventLoopGroup boosGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup(4);

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .group(boosGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    //                .option()
                    //                .childOption()
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ChannelPipeline pipeline = ch.pipeline();
                            //加入一个netty 提供 IdleStateHandler

                            /*
                            说明
                                1. IdleStateHandler 是netty 提供的处理空闲状态的处理器
                                2. long readerIdleTime : 表示多长时间没有读, 就会发送一个心跳检测包检测是否连接
                                3. long writerIdleTime : 表示多长时间没有写, 就会发送一个心跳检测包检测是否连接
                                4. long allIdleTime : 表示多长时间没有读写, 就会发送一个心跳检测包检测是否连接
                                5. 文档说明
                                    triggers an {@link IdleStateEvent} when a {@link Channel} has not performed read, write, or both operation for a while.
                                6. 当 IdleStateEvent 触发后 , 就会传递给管道 的下一个handler去处理通过调用(触发)下一个handler 的 userEventTiggered , 在该方法中去处理 IdleStateEvent(读空闲，写空闲，读写空闲)
                             */

                            pipeline.addLast(new IdleStateHandler(3, 7, 5, TimeUnit.SECONDS));
                            pipeline.addLast(new HeartBeatHandler());

                        }
                    });


            ChannelFuture channelFuture = serverBootstrap.bind(8090).sync();

            channelFuture.channel().closeFuture().sync();


        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }


    }

}
