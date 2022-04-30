package com.at._08_netty._17_tcp;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @create 2022-04-30
 */
public class TcpServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        System.out.println("server channel initializer...");

        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new TcpServerHandler());

    }
}
