package at._08_netty._19;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;

/**
 * @create 2023-05-28
 */
public class NettyServer {

    public static void main(String[] args) {

        EventLoopGroup boosGroup = null;
        EventLoopGroup workGroup = null;

        try {

            boosGroup = new NioEventLoopGroup(1); // server 线程
            workGroup = new NioEventLoopGroup(4); // client 线程（子线程）

            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap
                    .group(boosGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 64)
                    .childOption(ChannelOption.TCP_NODELAY,true)
                    .childAttr(AttributeKey.newInstance("childAtr"),1)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ServerHandler())
                    .childHandler(
                            new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel ch) throws Exception {

                                    System.out.println("server ChannelInitializer...");

                                    ChannelPipeline pipeline = ch.pipeline();

                                    //向pipeline加入解码器 编码器
                                    pipeline.addLast("decoder", new StringDecoder());
                                    pipeline.addLast("encoder", new StringEncoder());

                                    pipeline.addLast(new ServerSimpleChannelInboundHandler());

                                }
                            }
                    );


            ChannelFuture channelFuture = serverBootstrap.bind(9099).sync();
            System.out.println("服务端启动成功................");

            channelFuture.channel().closeFuture().sync();


        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }

//        服务端启动成功................
//        server ChannelInitializer...
//        ServerSimpleChannelInboundHandler 无参构造器
//        ChannelHandlerAdapter isSharable 		  ---------- 1
//        ChannelHandlerAdapter handlerAdded 		  ---------- 2
//        ChannelInboundHandlerAdapter channelRegistered 		  ---------- 3
//        ChannelInboundHandlerAdapter channelActive 		  ---------- 4
//        SimpleChannelInboundHandler channelRead
//        SimpleChannelInboundHandler acceptInboundMessage
//        SimpleChannelInboundHandler channelRead0
//        server 端接收到 client 端的 message： hello netty....
//        ChannelInboundHandlerAdapter channelReadComplete


    }
}

class ServerHandler extends ChannelInboundHandlerAdapter{

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive");
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelRegistered");
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerAdded");
    }
}


class ServerSimpleChannelInboundHandler extends SimpleChannelInboundHandler<String> {

    //-------------------------------------------------------------------------------
    // ChatServerHandler 构造器
    //-------------------------------------------------------------------------------


    protected ServerSimpleChannelInboundHandler() {
        super();
        System.out.println("ServerSimpleChannelInboundHandler 无参构造器"); // 1
    }

    protected ServerSimpleChannelInboundHandler(boolean autoRelease) {
        super(autoRelease);
        System.out.println("ServerSimpleChannelInboundHandler 1参构造器 ");
    }

    protected ServerSimpleChannelInboundHandler(Class<? extends String> inboundMessageType) {
        super(inboundMessageType);
    }

    protected ServerSimpleChannelInboundHandler(Class<? extends String> inboundMessageType, boolean autoRelease) {
        super(inboundMessageType, autoRelease);
        System.out.println("ServerSimpleChannelInboundHandler 2参构造器");
    }


    //-------------------------------------------------------------------------------
    // SimpleChannelInboundHandler
    //-------------------------------------------------------------------------------

    @Override
    public boolean acceptInboundMessage(Object msg) throws Exception {
        System.out.println("SimpleChannelInboundHandler acceptInboundMessage"); // 6
        return super.acceptInboundMessage(msg);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception { // 5
        System.out.println("SimpleChannelInboundHandler channelRead");
        super.channelRead(ctx, msg);

        String message = (String) msg;
        System.out.println("server 端接收到 client 端的 message： " + message); // 8

        ctx.writeAndFlush(message);

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("SimpleChannelInboundHandler channelRead0 "); // 7
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
        System.out.println("ChannelInboundHandlerAdapter channelActive \t\t  ---------- 4"); // 4
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ChannelInboundHandlerAdapter channelInactive");
        super.channelInactive(ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ChannelInboundHandlerAdapter channelReadComplete"); // 8
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
        System.out.println("ChannelHandlerAdapter isSharable \t\t  ---------- 1"); // 2
        return super.isSharable();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ChannelHandlerAdapter handlerAdded \t\t  ---------- 2"); // 3
        super.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("ChannelHandlerAdapter handlerRemoved");
        super.handlerRemoved(ctx);
    }


}