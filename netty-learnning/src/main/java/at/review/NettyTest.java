package at.review;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @create 2022-05-07
 */
public class NettyTest {


    public static void main(String[] args) throws Exception {

        // 创建 boosGroup 一直循环只处理连接请求，真正的业务交由 workGroup 处理
        EventLoopGroup boosGroup = new NioEventLoopGroup(1);
        // 创建 workGroup 处理 read write 事件
        EventLoopGroup workgroup = new NioEventLoopGroup(4);

        EventLoop eventLoop = workgroup.next();

        eventLoop.execute(() -> {
            System.out.println("获取一个 EventLoop 执行 111");
        });


        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap
                .group(boosGroup, workgroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 64)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(
new ChannelInitializer<SocketChannel>() {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast("logger", new LoggingHandler(LogLevel.INFO));

        // 1
        pipeline.addLast(workgroup, new StringDecoder());

        pipeline.addLast(new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

                Channel channel = ctx.channel();

                // 2
                channel.eventLoop().execute(() -> {
                    System.out.println("将耗时的操作交由 taskQueue 处理 提交多个任务还是一个线程在执行");
                });

                // 3
                channel.eventLoop().schedule(() -> {
                    System.out.println(" 将耗时的操作交由 scheduledTaskQueue 处理 提交多个任务使用不同的线程 ");
                }, 10, TimeUnit.SECONDS);

            }
        });


    }
}
                );
//io.netty.bootstrap.AbstractBootstrap.bind(java.net.SocketAddress)

        ChannelFuture channelFuture = serverBootstrap.bind("127.0.0.1", 8090).sync();


        System.out.println("..............................");


    }

}
