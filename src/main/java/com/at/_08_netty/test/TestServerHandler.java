package com.at._08_netty.test;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * @create 2022-04-27
 */
public class TestServerHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

        System.out.println("server 接到到 client 数据 = " + msg);

        ctx.writeAndFlush(Unpooled.copiedBuffer("server 回复 client 数据", CharsetUtil.UTF_8));


    }
}
