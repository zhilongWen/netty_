package com.at.rpc_.netty;

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
 * @create 2022-05-03
 */
public class NettyService {

    public static void start(String hostname,int port){


        EventLoopGroup boosGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup(1);

        try {
            ServerBootstrap b = new ServerBootstrap();

            b.group(boosGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,32)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(new StringEncoder());

                            pipeline.addLast(new NettyServiceHandler());

                        }
                    });

            ChannelFuture channelFuture = b.bind(hostname, port).sync();

//            channelFuture.addListener(new ChannelFutureListener() {
//                @Override
//                public void operationComplete(ChannelFuture future) throws Exception {
//                    if(future.isSuccess()) System.out.println("");
//                }
//            })

            channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }


    }

}
