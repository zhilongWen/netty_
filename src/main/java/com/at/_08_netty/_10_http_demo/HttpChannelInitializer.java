package com.at._08_netty._10_http_demo;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @create 2022-04-23
 */
public class HttpChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        System.out.println("初始化server端channel对象...");

        ChannelPipeline pipeline = ch.pipeline();


        /*
            加入一个netty 提供的httpServerCodec codec =>[coder - decoder]
                1. HttpServerCodec 是netty 提供的处理http的 编-解码器
                2. 增加一个自定义的handler
         */

        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpServerHandler());

        System.out.println("server channel initial ok...");





    }
}
