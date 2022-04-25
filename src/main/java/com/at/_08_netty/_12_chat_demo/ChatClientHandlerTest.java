package com.at._08_netty._12_chat_demo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @create 2022-04-25
 */
public class ChatClientHandlerTest extends SimpleChannelInboundHandler<String> {

    //-------------------------------------------------------------------------------
    // SimpleChannelInboundHandler
    //-------------------------------------------------------------------------------

    protected ChatClientHandlerTest() {
        super();
        System.out.println("ChatClientHandler 无参");
    }

    protected ChatClientHandlerTest(boolean autoRelease) {
        super(autoRelease);
        System.out.println("ChatClientHandler 1_1参");
    }

    protected ChatClientHandlerTest(Class<? extends String> inboundMessageType) {
        super(inboundMessageType);
        System.out.println("ChatClientHandler 1_2无参");
    }

    protected ChatClientHandlerTest(Class<? extends String> inboundMessageType, boolean autoRelease) {
        super(inboundMessageType, autoRelease);
        System.out.println("ChatClientHandler 2参");
    }

    @Override
    public boolean acceptInboundMessage(Object msg) throws Exception {
        System.out.println("SimpleChannelInboundHandler acceptInboundMessage");
        return super.acceptInboundMessage(msg);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        System.out.println("SimpleChannelInboundHandler channelRead");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("SimpleChannelInboundHandler channelRead0");
    }


    //-------------------------------------------------------------------------------
    // ChannelInboundHandlerAdapter
    //-------------------------------------------------------------------------------


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        System.out.println("ChannelInboundHandlerAdapter channelRegistered");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        System.out.println("ChannelInboundHandlerAdapter channelUnregistered");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("ChannelInboundHandlerAdapter channelActive");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        System.out.println("ChannelInboundHandlerAdapter channelInactive");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        System.out.println("ChannelInboundHandlerAdapter channelReadComplete");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        System.out.println("ChannelInboundHandlerAdapter userEventTriggered");
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
        System.out.println("ChannelInboundHandlerAdapter channelWritabilityChanged ");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        System.out.println("ChannelInboundHandlerAdapter exceptionCaught");
    }

    //-------------------------------------------------------------------------------
    // ChannelHandlerAdapter
    //-------------------------------------------------------------------------------


    @Override
    protected void ensureNotSharable() {
        super.ensureNotSharable();
        System.out.println("ChannelHandlerAdapter ensureNotSharable");
    }

    @Override
    public boolean isSharable() {
        System.out.println("ChannelHandlerAdapter isSharable");
        return super.isSharable();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        System.out.println("ChannelHandlerAdapter handlerAdded");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        System.out.println("ChannelHandlerAdapter handlerRemoved");
    }
}
