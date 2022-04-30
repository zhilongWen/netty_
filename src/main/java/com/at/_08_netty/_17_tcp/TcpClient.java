package com.at._08_netty._17_tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @create 2022-04-30
 */
public class TcpClient {

    public static void main(String[] args) throws Exception{

        EventLoopGroup group = new NioEventLoopGroup();


        try {

            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new TcpClientInitializer());

            System.out.println("client initializer ok...");

            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8090).sync();

            channelFuture.channel().closeFuture().sync();


        }finally {

            group.shutdownGracefully();

        }


    }

}
