package com.at.review;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;

/**
 * @create 2022-05-06
 */
public class NettyClient {

    public static void main(String[] args) {

        // 客户端需要一个事件循环组
        NioEventLoopGroup group = null;

        try {

            group = new NioEventLoopGroup();

            // 创建客户端启动对象
            Bootstrap b = new Bootstrap();

            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast(new ChannelInboundHandlerAdapter() {

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    System.out.println("当通道就绪就会触发该方法.....");
                                    ctx.writeAndFlush(Unpooled.copiedBuffer("client 通道已就绪...", CharsetUtil.UTF_8));
                                }

                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

                                    ByteBuf buffer = (ByteBuf) msg;

                                    System.out.println("client 读取 server 发送的数据 msg = " + buffer.toString(CharsetUtil.UTF_8));

                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    System.out.println("出现异常，异常信息 cause = " + cause.getMessage());
                                    // 关闭通道
                                    ctx.close();
                                }
                            });

                        }
                    });

            System.out.println("client 端已 ok ...");

            // 启动客户端去连接服务器端
            ChannelFuture channelFuture = b.connect("127.0.0.1", 8090).sync();

            channelFuture.channel().closeFuture().sync();


        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }


    }

}
