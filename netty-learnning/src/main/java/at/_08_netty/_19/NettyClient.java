package at._08_netty._19;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @create 2023-05-28
 */
public class NettyClient {

    public static void main(String[] args) {

        EventLoopGroup group = null;

        try {

            group = new NioEventLoopGroup();

            Bootstrap bootstrap = new Bootstrap();

            bootstrap
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 32)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            System.out.println("ChatClient ChannelInitializer");

                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast("encoder", new StringEncoder())
                                    .addLast("decoder", new StringDecoder());

                            pipeline.addLast(new ClientSimpleChannelInboundHandler());

                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 9099).sync();

            System.out.println("client 启动成功.................");

            channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }

//        ChatClient ChannelInitializer
//        ClientSimpleChannelInboundHandler 无参
//        ChannelHandlerAdapter isSharable
//        ChannelHandlerAdapter handlerAdded
//        ChannelInboundHandlerAdapter channelRegistered
//        client 启动成功.................
//        ChannelInboundHandlerAdapter channelActive
//        发送给 server 的信息：hello netty....
//        SimpleChannelInboundHandler acceptInboundMessage
//        SimpleChannelInboundHandler channelRead0
//        SimpleChannelInboundHandler channelRead
//        client 端处理 server 端的信息：hello netty....
//        ChannelInboundHandlerAdapter channelReadComplete


    }

}

class ClientSimpleChannelInboundHandler extends SimpleChannelInboundHandler<String> {

    //-------------------------------------------------------------------------------
    // SimpleChannelInboundHandler
    //-------------------------------------------------------------------------------

    protected ClientSimpleChannelInboundHandler() {
        super();
        System.out.println("ClientSimpleChannelInboundHandler 无参"); // 1
    }

    protected ClientSimpleChannelInboundHandler(boolean autoRelease) {
        super(autoRelease);
        System.out.println("ClientSimpleChannelInboundHandler 1_1参");
    }

    protected ClientSimpleChannelInboundHandler(Class<? extends String> inboundMessageType) {
        super(inboundMessageType);
        System.out.println("ClientSimpleChannelInboundHandler 1_2无参");
    }

    protected ClientSimpleChannelInboundHandler(Class<? extends String> inboundMessageType, boolean autoRelease) {
        super(inboundMessageType, autoRelease);
        System.out.println("ClientSimpleChannelInboundHandler 2参");
    }

    @Override
    public boolean acceptInboundMessage(Object msg) throws Exception {
        System.out.println("SimpleChannelInboundHandler acceptInboundMessage"); // 6
        return super.acceptInboundMessage(msg);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        System.out.println("SimpleChannelInboundHandler channelRead"); // 8


        String message = (String) msg;

        System.out.println("client 端处理 server 端的信息：" + message); // 8-1


    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("SimpleChannelInboundHandler channelRead0"); // 7
    }


    //-------------------------------------------------------------------------------
    // ChannelInboundHandlerAdapter
    //-------------------------------------------------------------------------------


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        System.out.println("ChannelInboundHandlerAdapter channelRegistered"); // 4
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        System.out.println("ChannelInboundHandlerAdapter channelUnregistered");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("ChannelInboundHandlerAdapter channelActive"); // 5

        String msg = "hello netty....";
        System.out.println("发送给 server 的信息：" + msg);
        ctx.writeAndFlush(msg);

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        System.out.println("ChannelInboundHandlerAdapter channelInactive");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        System.out.println("ChannelInboundHandlerAdapter channelReadComplete"); // 9
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
        System.out.println("ChannelHandlerAdapter isSharable"); // 2
        return super.isSharable();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        System.out.println("ChannelHandlerAdapter handlerAdded"); // 3
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        System.out.println("ChannelHandlerAdapter handlerRemoved");
    }
}