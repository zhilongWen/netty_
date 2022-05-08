package com.at.review;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.List;

/**
 * @create 2022-05-09
 */
public class NettyHandlerServer {


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
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ChannelPipeline pipeline = ch.pipeline();

                            // 添加一个入站的解码器
//                            pipeline.addLast(new ByteToMessageDecoder() {
//                                @Override
//                                protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
//
//                                    System.out.println("server 端解码器 ByteToMessageDecoder 被调用~~~~");
//
//                                    // 因为 long 8个字节, 需要判断有8个字节，才能读取一个long
//                                    if (in.readableBytes() >= 8) out.add(in.readLong());
//
//                                }
//                            });


                            pipeline.addLast(new ReplayingDecoder<Void>() {

                                // public abstract class ReplayingDecoder<S> extends ByteToMessageDecoder

                                @Override
                                protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

                                    System.out.println("server 端解码器 ReplayingDecoder 被调用~~~~");

                                    // ReplayingDecoder 无需判断数据是否足够读取，内部会进行判断
                                    out.add(in.readLong());

                                }
                            });

                            // 添加自定义 handler
                            pipeline.addLast(new SimpleChannelInboundHandler<Long>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {

                                    System.out.println("自定义 handler SimpleChannelInboundHandler 被调用。。。。");

                                    System.out.println("从 client：" + ctx.channel().remoteAddress() + " 读取到数据 msg = " + msg);

                                    // 回复一条信息

                                }

                                @Override
                                public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
                                    super.handlerAdded(ctx);
                                    System.out.println("channel：" + ctx.channel().hashCode() + " 加入 pipline：" + ctx.pipeline().hashCode());
                                }

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    super.channelActive(ctx);
                                    System.out.println("channel：" + ctx.channel().hashCode() + " 成功建立连接 ");
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    super.exceptionCaught(ctx, cause);
                                    ctx.close();
                                }


                            });


                        }
                    });

            ChannelFuture channelFuture = serverBootstrap.bind("127.0.0.1", 8090).sync();

            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) System.out.println("server 监听 8090 端口成功");
                    else System.out.println("server 监听 8090 端口失败");
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
