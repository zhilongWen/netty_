package com.at._08_netty._12_chat_demo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

import javax.xml.transform.Source;

/**
 * @create 2022-04-25
 */
//public class ChatServerHandler extends ChannelInboundHandlerAdapter {
//
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        super.channelRead(ctx, msg);
//    }
//}


public class ChatServerHandlerTest extends SimpleChannelInboundHandler<String> {

    //-------------------------------------------------------------------------------
    // ChatServerHandler 构造器
    //-------------------------------------------------------------------------------


    protected ChatServerHandlerTest() {
        super();
        System.out.println("ChatServerHandler 无参构造器");
    }

    protected ChatServerHandlerTest(boolean autoRelease) {
        super(autoRelease);
        System.out.println("ChatServerHandler 1参构造器 ");
    }

    protected ChatServerHandlerTest(Class<? extends String> inboundMessageType) {
        super(inboundMessageType);
    }

    protected ChatServerHandlerTest(Class<? extends String> inboundMessageType, boolean autoRelease) {
        super(inboundMessageType, autoRelease);
        System.out.println("ChatServerHandler 2参构造器");
    }


    //-------------------------------------------------------------------------------
    // SimpleChannelInboundHandler
    //-------------------------------------------------------------------------------

    @Override
    public boolean acceptInboundMessage(Object msg) throws Exception {
        System.out.println("SimpleChannelInboundHandler acceptInboundMessage");
        return super.acceptInboundMessage(msg);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("SimpleChannelInboundHandler channelRead");
        super.channelRead(ctx, msg);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("SimpleChannelInboundHandler channelRead0 ");
    }


    //-------------------------------------------------------------------------------
    // ChannelInboundHandlerAdapter
    //-------------------------------------------------------------------------------

    @Override

    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ChannelInboundHandlerAdapter channelRegistered \t\t  ---------- 3");
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ChannelInboundHandlerAdapter channelUnregistered ");
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ChannelInboundHandlerAdapter channelActive \t\t  ---------- 4");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ChannelInboundHandlerAdapter channelInactive");
        super.channelInactive(ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ChannelInboundHandlerAdapter channelReadComplete");
        super.channelReadComplete(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("ChannelInboundHandlerAdapter userEventTriggered");
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ChannelInboundHandlerAdapter channelWritabilityChanged ");
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("ChannelInboundHandlerAdapter exceptionCaught");
        super.exceptionCaught(ctx, cause);
    }

    //-------------------------------------------------------------------------------
    // ChannelHandlerAdapter
    //-------------------------------------------------------------------------------

    @Override
    protected void ensureNotSharable() {
        System.out.println("ChannelHandlerAdapter ensureNotSharable");
        super.ensureNotSharable();
    }

    @Override
    public boolean isSharable() {
        System.out.println("ChannelHandlerAdapter isSharable \t\t  ---------- 1");
        return super.isSharable();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ChannelHandlerAdapter handlerAdded \t\t  ---------- 2");
        super.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ChannelHandlerAdapter handlerRemoved");
        super.handlerRemoved(ctx);
    }



}