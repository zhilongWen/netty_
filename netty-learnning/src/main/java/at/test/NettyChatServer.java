package at.test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DateFormatter;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import io.netty.util.NetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * @create 2023-06-06
 */
public class NettyChatServer {

    public static final String HOST = "127.0.0.1";

    public static final Integer PORT = 9099;

    public static void main(String[] args) {

        NioEventLoopGroup boosGroup = null;
        NioEventLoopGroup workGroup = null;

        try {

            boosGroup = new NioEventLoopGroup(1);
            workGroup = new NioEventLoopGroup();

            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap
                    .group(boosGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,64)
                    .option(ChannelOption.ALLOCATOR, ByteBufAllocator.DEFAULT)
                    .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .attr(AttributeKey.newInstance("abc"),"abc")
                    .childAttr(AttributeKey.newInstance("childABC"),"childABC")
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new SelfChannelInitializer());

            serverBootstrap.bind(HOST,PORT).sync().channel().closeFuture().sync();


        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }


    }

}

class SelfChannelInitializer extends ChannelInitializer<SocketChannel>{

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new StringDecoder())
                .addLast(new StringEncoder())
                .addLast(new SelfSimpleChannelInboundHandler());

    }
}

class SelfSimpleChannelInboundHandler extends SimpleChannelInboundHandler<String>{

    private static final ChannelGroup GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("有新连接接入 server handlerAdded 被回调...");

        Channel channel = ctx.channel();

        GROUP.writeAndFlush("[ client ] " + channel.remoteAddress() + " 加入聊天 + " + formatter.format(LocalDateTime.now()));

        GROUP.add(channel);

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

        Channel channel = ctx.channel();

        GROUP.writeAndFlush("[client] " + channel.remoteAddress() + " 离开了!!!!");

        System.out.println("当前 channelGroup size = " + GROUP.size());

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

        GROUP.stream().filter(ch -> ch!=channel).forEach(ch -> ch.writeAndFlush(data));

    }
}


