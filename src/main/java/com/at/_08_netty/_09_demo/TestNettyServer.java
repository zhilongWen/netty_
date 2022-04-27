package com.at._08_netty._09_demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

import java.util.Optional;

/**
 * @create 2022-04-21
 */
public class TestNettyServer {

    public static void main(String[] args) {

        EventLoopGroup boosGroup = null;
        EventLoopGroup workGroup = null;

        try {

            /*

                boosGroup,workGroup 含有的子线程（NioEventLoop）的个数默认为机器CPU核数的2倍 使用EventExecutor线程组管理

                boosGroup将数据循环的发送给workGroup的各个线程

             */

            //创建 BoosGroup 工作线程
            //bossGroup 一直循环 只是处理连接请求 , 真正的和客户端业务处理，会交给 workerGroup完成
            boosGroup = new NioEventLoopGroup(2);

            //创建 WorkGroup 工作线程组
            workGroup = new NioEventLoopGroup(4);


            //配置服务端参数
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(boosGroup,workGroup)  // 配置boosGroup workGroup
                    .channel(NioServerSocketChannel.class) //使用NioSocketChannel 作为服务器的通道实现
                    .option(ChannelOption.SO_BACKLOG,64) //设置线程队列得到连接个数
                    .childOption(ChannelOption.SO_KEEPALIVE,true) //设置保持活动连接状态
//                    .handler(null) // handler 在 BoosGroup 中生效    childHandler 在 WorkGroup 中生效
                    .childHandler(new ChannelInitializer<SocketChannel>() {  //创建一个通道初始化对象
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {

                            System.out.println("初始化server端channel对象...");

//                            socketChannel.pipeline().addLast(new TestNettyServerHandler());


                            socketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter(){

                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {


                                    System.out.println("server端线程：" + Thread.currentThread().getName());

                                    System.out.println("server ChannelHandlerContext：" + ctx);



                                    Channel channel = ctx.channel();
                                    ChannelPipeline pipeline = ctx.pipeline();


                                    System.out.println("server doing...");




                                }


                                @Override
                                public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

                                    ctx.writeAndFlush(Unpooled.copiedBuffer("server端给client端提示提示信息：server 端已读取 client 端发送的信息... ", CharsetUtil.UTF_8));

                                }



                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    cause.printStackTrace();
                                    ctx.close();
                                }
                            });


                        }
                    });


            System.out.println("server端 is ready ...");


            //绑定端口 生成一个ChannelFuture 对象
            ChannelFuture channelFuture = serverBootstrap.bind(9089).sync();

            /*
                通过 isDone 方法来判断当前操作是否完成；
                通过 isSuccess 方法来判断已完成的当前操作是否成功；
                通过 getCause 方法来获取已完成的当前操作失败的原因；
                通过 isCancelled 方法来判断已完成的当前操作是否被取消；
                通过 addListener 方法来注册监听器，当操作已完成(isDone 方法返回完成)，将会通知 指定的监听器；如果 Future 对象已完成，则通知指定的监听器
             */
            //给 ChannelFuture 注册监听器
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(future.isSuccess()){
                        System.out.println("server 端监听端口成功...");
                    }else {
                        System.out.println("server 端监听端口失败...");
                    }
                }
            });

            //关闭通道
            channelFuture.channel().closeFuture().sync();


        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

            Optional.ofNullable(boosGroup).ifPresent(bg -> bg.shutdownGracefully());
            Optional.ofNullable(workGroup).ifPresent(wg -> wg.shutdownGracefully());

        }


    }

}
