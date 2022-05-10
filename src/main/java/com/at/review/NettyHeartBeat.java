package com.at.review;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @create 2022-05-10
 */
public class NettyHeartBeat {

    public static void main(String[] args) {

        // 127.0.0.1:8090


        EventLoopGroup boosGroup = null;
        EventLoopGroup workGroup = null;


        try {

            boosGroup = new NioEventLoopGroup(1);
            workGroup = new NioEventLoopGroup(4);

            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .group(boosGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
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

                            pipeline.addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

                                    super.userEventTriggered(ctx, evt);

                                    if (evt instanceof IdleStateEvent) {

                                        IdleStateEvent event = (IdleStateEvent) evt;

                                        String eventType = null;

                                        switch (event.state()) {
                                            case READER_IDLE:
                                                eventType = "读空闲";
                                                break;
                                            case WRITER_IDLE:
                                                eventType = "写空闲";
                                                break;
                                            case ALL_IDLE:
                                                eventType = "读写空闲";
                                                break;
                                        }
                                        System.out.println(ctx.channel().remoteAddress() + "--超时时间--" + eventType);
                                        System.out.println("服务器做相应处理..");

                                        //如果发生空闲，我们关闭通道
                                        // ctx.channel().close();

                                    }

                                }
                            });

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
