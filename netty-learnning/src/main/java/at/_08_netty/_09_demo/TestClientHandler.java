package at._08_netty._09_demo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * @create 2022-04-21
 */
public class TestClientHandler extends ChannelInboundHandlerAdapter {


    /**
     *
     * 当通道就绪就会触发该方法
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        System.out.println("client ChannelHandlerContext：" + ctx);
        ctx.writeAndFlush(Unpooled.copiedBuffer("client 端通道已就绪...", CharsetUtil.UTF_8));

    }


    /**
     *
     * 当通道有读取事件时，会触发
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf info = (ByteBuf) msg;
        System.out.println("client端收到server端回复的信息：" + info.toString(CharsetUtil.UTF_8));
        System.out.println("服务器的地址：" + ctx.channel().remoteAddress());

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.channel().close();
    }
}
