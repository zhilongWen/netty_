package com.at.rpc.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.Callable;

/**
 * @create 2022-05-03
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter implements Callable {

    private ChannelHandlerContext ctx;
    private String result;
    private String param;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client 与 server channel 建立连接");
        this.ctx = ctx;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("client NettyClientHandler 出现异常：" + cause.getMessage());
        ctx.close();
    }

    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        System.out.println("读取 channel 信息 msg = "+ msg);

        result = msg.toString();

        notify();

    }

    @Override
    public synchronized Object call() throws Exception {

        System.out.println("call-1....");

        ctx.writeAndFlush(param);

        wait();

        System.out.println("call-2...");

        return result;
    }

    public void setParam(String param){
        System.out.println("setParam。。。。");
        this.param = param;
    }

}
