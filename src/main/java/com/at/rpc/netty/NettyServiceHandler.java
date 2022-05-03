package com.at.rpc.netty;

import com.at.rpc.provider.ServiceImpl;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @create 2022-05-03
 */
public class NettyServiceHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        System.out.println("client 端 信息 msg = " + msg);

        String result = new ServiceImpl().hello(msg.toString());

        ctx.writeAndFlush(result);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
