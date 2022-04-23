package com.at._08_netty._09_demo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * @create 2022-04-21
 *
 *  自定义一个 handler
 *
 */
public class TestNettyServerHandler extends ChannelInboundHandlerAdapter {


    /**
     *
     * 读取客户端发送的数据
     *
     * @param ctx  上下文对象, 含有 管道pipeline , 通道channel, 地址
     * @param msg  就是客户端发送的数据 默认Object
     * @throws Exception
     *
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {


        System.out.println("server端线程：" + Thread.currentThread().getName());

        System.out.println("server ChannelHandlerContext：" + ctx);

        /*
            pipeline 本质是一个双向链表

            channel 与 pipeline 是一个相互包含的关系 通过一个可以得到另一个

         */

        Channel channel = ctx.channel();
        ChannelPipeline pipeline = ctx.pipeline();


//        ByteBuf info = (ByteBuf) msg;
//        System.out.println("client 端发送的数据：" + info.toString(CharsetUtil.UTF_8));
//        System.out.println("client 端发送的地址：" + ctx.channel().remoteAddress());


        //模拟一个耗时的操作

//        try { TimeUnit.SECONDS.sleep(5); } catch (InterruptedException e) { e.printStackTrace(); }
//
//        ctx.writeAndFlush(Unpooled.copiedBuffer("server端接收到client端的数据，server端耗时10s完成...",CharsetUtil.UTF_8));
//
//        System.out.println("server端处理完了client端的数据...");


        /*
            处理耗时操作
                1.用户程序自定义普通任务
                    将任务提交 taskQueue中 但还是一个线程在执行
                2.定时提交任务
                    将任务提交 scheduledTaskQueue 使用不同的线程

         */

        //1.用户程序自定义普通任务

        //task1  10s后执行
      /*  ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(10);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("server端接收到client端的数据，server端耗时10s完成...",CharsetUtil.UTF_8));
                    System.out.println("server端处理完了client端的数据...");
                } catch (InterruptedException e) { e.printStackTrace(); }
            }
        });

        //task2 20s后执行
        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(10);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("server端接收到client端的数据，server端耗时10s完成...",CharsetUtil.UTF_8));
                    System.out.println("20s");
                    System.out.println("server端处理完了client端的数据...");
                } catch (InterruptedException e) { e.printStackTrace(); }
            }
        });
*/

        //2.定时提交任务
        /*ctx.channel().eventLoop().schedule(() -> {
            ctx.writeAndFlush(Unpooled.copiedBuffer("2.定时提交任务1",CharsetUtil.UTF_8));
            System.out.println("方案2 将任务提交到 scheduledTaskQueue");
        },5,TimeUnit.SECONDS);

        ctx.channel().eventLoop().schedule(() -> {
            ctx.writeAndFlush(Unpooled.copiedBuffer("2.定时提交任务2",CharsetUtil.UTF_8));
            System.out.println("方案2 将任务提交到 scheduledTaskQueue");
        },5,TimeUnit.SECONDS);*/


        System.out.println("server doing...");





    }


    /**
     *
     * 数据读取完毕
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

        ctx.writeAndFlush(Unpooled.copiedBuffer("server端给client端提示提示信息：server 端已读取 client 端发送的信息... ",CharsetUtil.UTF_8));

    }


    /**
     *
     * 处理异常, 一般是需要关闭通道
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
