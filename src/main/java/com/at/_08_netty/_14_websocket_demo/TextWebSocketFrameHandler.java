package com.at._08_netty._14_websocket_demo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.time.LocalDateTime;

/**
 *
 *
 *
 * @create 2022-04-25
 */

//这里 TextWebSocketFrame 类型，表示一个文本帧(frame)
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    /**
     * 当web客户端连接后， 触发方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);

        //id 表示唯一的值，LongText 是唯一的 ShortText 不是唯一
        System.out.println("handlerAdded 被调用 asLongText ：" + ctx.channel().id().asLongText());
        System.out.println("handlerAdded 被调用 asShortText ：" + ctx.channel().id().asShortText());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);

        System.out.println("handlerRemoved 被调用：" + ctx.channel().id().asLongText());

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("发生异常：" + cause);
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

        System.out.println("server 接收到的数据;" + msg.text());

        //回复消息给client
        ctx.channel().writeAndFlush(new TextWebSocketFrame("server 端 time：" + LocalDateTime.now() + " " + msg.text()));

    }
}
