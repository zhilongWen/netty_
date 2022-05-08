package com.at.review;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @create 2022-05-07
 */
public class NettyTest {


    public static void main(String[] args) {

        // 创建 boosGroup 一直循环只处理连接请求，真正的业务交由 workGroup 处理
        EventLoopGroup boosGroup = new NioEventLoopGroup(1);
        // 创建 workGroup 处理 read write 事件
        EventLoopGroup workgroup = new NioEventLoopGroup(4);


        EventLoop eventLoop = workgroup.next();

        eventLoop.execute(() -> {
            System.out.println("获取一个 EventLoop 执行 111");
        });


        ServerBootstrap serverBootstrap = new ServerBootstrap()
                .group(boosGroup, workgroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {

                    }
                });




        System.out.println("..............................");


    }

}
