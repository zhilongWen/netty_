package com.at.review;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @create 2022-05-07
 */
public class NettyChatServerHandler extends SimpleChannelInboundHandler<String> {

    // 定义一个 channel 组，管理所有的channel
    // GlobalEventExecutor.INSTANCE) 是全局的事件执行器，是一个单例
    public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssS");


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

        System.out.println("server self handler handlerAdded 被调用~~~~" );

        // 一旦连接，第一个被执行

        Channel channel = ctx.channel();

        /*

            将该客户加入聊天的信息推送给其它在线的客户端


            该方法会将 channelGroup 中所有的channel 遍历，并发送 消息， 无需要自己遍历
            public ChannelGroupFuture writeAndFlush(Object message, ChannelMatcher matcher, boolean voidPromise) {
                if (message == null) {
                    throw new NullPointerException("message");
                }

                final ChannelGroupFuture future;
                if (voidPromise) {
                    for (Channel c: nonServerChannels.values()) {
                        if (matcher.matches(c)) {
                            c.writeAndFlush(safeDuplicate(message), c.voidPromise());
                        }
                    }
                    future = voidFuture;
                } else {
                    Map<Channel, ChannelFuture> futures = new LinkedHashMap<Channel, ChannelFuture>(size());
                    for (Channel c: nonServerChannels.values()) { // 遍历，并发送 消息
                        if (matcher.matches(c)) {
                            futures.put(c, c.writeAndFlush(safeDuplicate(message)));
                        }
                    }
                    future = new DefaultChannelGroupFuture(this, futures, executor);
                }
                ReferenceCountUtil.release(message);
                return future;
            }
         */
        channelGroup.writeAndFlush("[client] " + channel.remoteAddress() + " 加入聊天 " + sdf.format(new Date()));

        // 将当前channel 加入到  channelGroup
        channelGroup.add(channel);

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

        System.out.println("server self handler handlerRemoved 被调用~~~~" );

        // 断开连接, 将xx客户离开信息推送给当前在线的客户

        Channel channel = ctx.channel();

        channelGroup.writeAndFlush("[client] " + channel.remoteAddress() + " 离开了!!!!");

        System.out.println("当前 channelGroup size = " + channelGroup.size());

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        System.out.println("server self handler channelActive 被调用~~~~" );

        System.out.println("[client] " + ctx.channel().remoteAddress() + " 上线了..." );

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        System.out.println("server self handler channelInactive 被调用~~~~" );

        System.out.println("[client] " + ctx.channel().remoteAddress() + " 离线了..." );
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        System.out.println("server self handler exceptionCaught 被调用~~~~" );

        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

        Channel channel = ctx.channel();

        String data = "[client] " + channel.remoteAddress() + " 的信息 msg = " + msg;

        System.out.println("[server] --> " + data);

        channelGroup.stream().filter(ch -> ch!=channel).forEach(ch -> ch.writeAndFlush(data));


    }


}
