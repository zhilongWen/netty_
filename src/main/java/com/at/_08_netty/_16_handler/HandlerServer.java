package com.at._08_netty._16_handler;

import com.sun.corba.se.internal.Interceptors.PIORB;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.List;

/**
 * @create 2022-04-27
 */
public class HandlerServer {

    /*

                        出栈
        client  -------------------->   server
                <--------------------
                        入栈
     */

    public static void main(String[] args) throws Exception {


        EventLoopGroup boosGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup(4);


        try {

            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .group(boosGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 64)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            System.out.println("server ChannelInitializer 被调用~~~");

                            ChannelPipeline pipeline = ch.pipeline();

                            //添加一个入栈的解码器 MessageToByteEncoder
                            pipeline.addLast(new ByteToMessageDecoder() {
                                /**
                                 *  decode 方法会根据接收的数据 被调用多次 直到没有新的元素被添加到 list 中
                                 *  或者 ByteBuf 没有更多的可读字节
                                 *
                                 *  如果 list 不为空 就会将 list 传递给下一个 ChannelInboundHandler 处理 该处理器的方法也会被调用多次
                                 *
                                 * @param ctx
                                 * @param in 入站的 ByteBuf
                                 * @param out List 集合，将解码后的数据传给下一个handler
                                 * @throws Exception
                                 */
                                @Override
                                protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

                                    System.out.println("server 端解码器 ByteToMessageDecoder 被调用~~~");

                                    //因为 long 8个字节, 需要判断有8个字节，才能读取一个long
                                    if(in.readableBytes() >= 8) out.add(in.readLong());

                                }
                            });

                            //添加自定义处理handler 处理client中的数据
                            pipeline.addLast(new SimpleChannelInboundHandler<Long>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {

                                    System.out.println("server 自定义处理handler~~~");

                                    System.out.println("从 client：" + ctx.channel().remoteAddress() + " 读取到数据 = " + msg);

                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    cause.printStackTrace();
                                    ctx.close();
                                }
                            });

                            System.out.println(".....////");


                        }
                    });


            System.out.println("server initializer ok...");

            ChannelFuture channelFuture = serverBootstrap.bind(8091).sync();

            channelFuture.addListener(future -> {
                if (future.isSuccess()) System.out.println("server 监听 8090 端口成功");
                else System.out.println("server 监听 8090 s端口失败");
            });

            channelFuture.channel().closeFuture().sync();

        } finally {
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }


    }

}
