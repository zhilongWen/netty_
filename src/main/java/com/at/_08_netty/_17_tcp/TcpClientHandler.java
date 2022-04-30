package com.at._08_netty._17_tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * @create 2022-04-30
 */
public class TcpClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private int count;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        // 使用客户端发送10条数据 hello,server
        for (int i = 0; i < 10; i++) {
            ctx.writeAndFlush(Unpooled.copiedBuffer("hello,server=" + i, CharsetUtil.UTF_8));
        }


    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {

        System.out.println("client 自定义 handler...");

        byte[] info = new byte[msg.readableBytes()];

        System.out.println("client 收到的消息：" + new String(info,CharsetUtil.UTF_8));
        System.out.println("client 收到的消息数量=" + (++this.count) + "\t");


    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }
}
