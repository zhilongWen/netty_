package at._08_netty.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * @create 2022-04-27
 */
public class TestClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf buf = (ByteBuf) msg;

        System.out.println("client 读取server端回复的信息：" + buf.toString(CharsetUtil.UTF_8));

    }

    @Override
    public void channelActive (ChannelHandlerContext ctx) throws Exception {

        ctx.writeAndFlush(Unpooled.copiedBuffer("client 端发送的数据", CharsetUtil.UTF_8));

    }
}
