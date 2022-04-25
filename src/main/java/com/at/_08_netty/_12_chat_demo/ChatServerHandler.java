package com.at._08_netty._12_chat_demo;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @create 2022-04-25
 */
public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

    //定义一个channle 组，管理所有的channel
    //GlobalEventExecutor.INSTANCE) 是全局的事件执行器，是一个单例
    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssS");


    /**
     *
     * 一旦连接，第一个被执行
     * 1.将当前channel 加入到  channelGroup
     * 2.将该客户加入聊天的信息推送给其它在线的客户端
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

        super.handlerAdded(ctx);

        Channel channel = ctx.channel();




        //将该客户加入聊天的信息推送给其它在线的客户端
        //该方法会将 channelGroup 中所有的channel 遍历，并发送 消息， 无需要自己遍历
        channelGroup.writeAndFlush("[client] " + channel.remoteAddress() + " 加入聊天 " + sdf.format(new Date()));


        //将当前channel 加入到  channelGroup
        channelGroup.add(channel);

    }

    /**
     *
     * 断开连接, 将xx客户离开信息推送给当前在线的客户
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

        super.handlerRemoved(ctx);

        Channel channel = ctx.channel();

        channelGroup.writeAndFlush("[client] " + channel.remoteAddress() + " 离开了！！！");
        System.out.println("channelGroup size：" + channelGroup.size());

    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        super.channelActive(ctx);

        System.out.println(ctx.channel().remoteAddress() + " 上线了...");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        System.out.println(ctx.channel().remoteAddress() + " 离线了~~~" );

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

        Channel channel = ctx.channel();

        channelGroup.forEach(ch -> {
            if(ch != channel){
                //不是当前的channel,转发消息
                ch.writeAndFlush("[client] " + channel.remoteAddress() + " 发送了消息 " + msg + "\n");
            }else {
                //回显自己发送的消息给自己
                ch.writeAndFlush("[自己]发送了消息" + msg + "\n");
            }
        });

    }



}
