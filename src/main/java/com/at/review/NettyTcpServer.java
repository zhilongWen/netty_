package com.at.review;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;

import java.util.UUID;

/**
 * @create 2022-05-10
 */
public class NettyTcpServer {

    public static void main(String[] args) {

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

                        private int count;

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {

                                    System.out.println("server 自定义 handler .....");

                                    byte[] buffer = new byte[msg.readableBytes()];

//                                    msg.readBytes(buffer);

                                    System.out.println("server 端收到的信息：" + new String(buffer, CharsetUtil.UTF_8));
                                    ctx.writeAndFlush(Unpooled.copiedBuffer(UUID.randomUUID().toString(), CharsetUtil.UTF_8));


                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    ctx.close();
                                }
                            });

                        }
                    });

            ChannelFuture channelFuture = serverBootstrap.bind("127.0.0.1", 8090).sync();

            channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }


    }

}
