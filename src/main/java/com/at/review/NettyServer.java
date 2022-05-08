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

import java.util.concurrent.TimeUnit;


/**
 * @create 2022-05-06
 */
public class NettyServer {

    public static void main(String[] args) {


        EventLoopGroup boosGroup = null;
        EventLoopGroup workGroup = null;


//        EventLoop next = boosGroup.next();

        try {

            // 创建 boosGroup 一直循环只处理连接请求，真正的业务交由 workGroup 处理
            boosGroup = new NioEventLoopGroup(1);

            // 创建 workGroup 处理 read write 事件
            workGroup = new NioEventLoopGroup(4);

            ServerBootstrap b = new ServerBootstrap();

            b.group(boosGroup, workGroup) // 配置boosGroup workGroup
                    .channel(NioServerSocketChannel.class) // 使用NioSocketChannel 作为服务器的通道实现
                    .option(ChannelOption.SO_BACKLOG, 64) // 设置线程队列得到连接个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true) // 设置保持活动连接状态
                    .handler(new LoggingHandler(LogLevel.INFO)) // handler 在 BoosGroup 中生效
                    .childHandler(new ChannelInitializer<SocketChannel>() { // childHandler 在 WorkGroup 中生效
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            System.out.println("初始化server端channel对象...");

                            System.out.println("ChannelInitializer thread name = " + Thread.currentThread().getName());

                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast(new ChannelInboundHandlerAdapter() {
                                /**
                                 * 读取客户端发送的数据
                                 * @param ctx 上下文对象, 含有 管道pipeline , 通道channel, 地址 等
                                 * @param msg 客户端发送的数据 默认Object
                                 * @throws Exception
                                 */
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

                                    System.out.println("server 端正在接收 client 端的数据......");

                                    System.out.println("ChannelInitializer channelRead thread name = " + Thread.currentThread().getName());


                                    System.out.println("ChannelInitializer channelRead 普通handle thread name = " + Thread.currentThread().getName());
                                    ByteBuf buffer0 = (ByteBuf) msg;
                                    System.out.println("client：" + ctx.channel().remoteAddress() + " 普通handle 发送过来的数据 msg = " + buffer0.toString(CharsetUtil.UTF_8));


                                    ctx.channel().eventLoop().execute(() -> {
                                        try {
                                            TimeUnit.SECONDS.sleep(5);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        System.out.println("ChannelInitializer channelRead taskQueue handle thread name = " + Thread.currentThread().getName());
                                        ByteBuf buffer1 = (ByteBuf) msg;
                                        System.out.println("client：" + ctx.channel().remoteAddress() + " 发送过来的数据 msg = " + buffer1.toString(CharsetUtil.UTF_8));
                                    });


                                    ctx.channel().eventLoop().schedule(() -> {
                                        System.out.println("ChannelInitializer channelRead scheduleQueue handle thread name = " + Thread.currentThread().getName());
                                        ByteBuf buffer2 = (ByteBuf) msg;
                                        System.out.println("client：" + ctx.channel().remoteAddress() + " 发送过来的数据 msg = " + buffer2.toString(CharsetUtil.UTF_8));
                                    }, 60, TimeUnit.SECONDS);

                                    ctx.channel().eventLoop().schedule(() -> {
                                        System.out.println("ChannelInitializer channelRead scheduleQueue handle thread name = " + Thread.currentThread().getName());
                                        ByteBuf buffer2 = (ByteBuf) msg;
                                        System.out.println("client：" + ctx.channel().remoteAddress() + " 发送过来的数据 msg = " + buffer2.toString(CharsetUtil.UTF_8));
                                    }, 60, TimeUnit.SECONDS);

                                    System.out.println("server doing...");

                                }

                                @Override
                                public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                                    System.out.println("server 数据读取完毕 .....");
                                    ctx.writeAndFlush(Unpooled.copiedBuffer("server 以读取 client 发送的数据...", CharsetUtil.UTF_8));
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    System.out.println("出现异常：" + cause.getMessage());
                                    // 关闭通道
                                    ctx.close();
                                }
                            });

                        }
                    });

            System.out.println("server 端 ready !!!!");

            ChannelFuture channelFuture = b.bind("127.0.0.1", 8090).sync();

            // 给 ChannelFuture 注册监听器
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) System.out.println("server 端监听 8090 端口 成功....");
                    else System.out.println("server 端监听 8090 端口 失败....");
                }
            });

            //关闭通道
            channelFuture.channel().closeFuture().sync();

            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {

                }
            });


        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();

        }


    }

}

