package com.at._08_netty._09_demo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Optional;

/**
 * @create 2022-04-21
 */
public class TestNettyClient {

    public static void main(String[] args) {

        //客户端需要一个事件循环组
        EventLoopGroup eventExecutors = null;

        try {
            eventExecutors = new NioEventLoopGroup();

            //创建客户端启动对象
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(eventExecutors)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new TestClientHandler());
                        }
                    });

            System.out.println("client 端已 ok ...");

            //启动客户端去连接服务器端
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 9089);

            channelFuture.channel().closeFuture().sync();


        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

            Optional.ofNullable(eventExecutors).ifPresent(f -> f.shutdownGracefully());

        }


    }

}
