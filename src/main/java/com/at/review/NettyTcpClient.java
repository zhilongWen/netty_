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
 * @create 2022-05-10
 */
public class NettyTcpClient {

    public static void main(String[] args) {

        EventLoopGroup group = null;

        try {
            group = new NioEventLoopGroup();

            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast(new SimpleChannelInboundHandler<ByteBuf>() {

                                private int count;

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    // 使用客户端发送10条数据 hello,server
                                    for (int i = 0; i < 10; i++) {
                                        ctx.writeAndFlush(Unpooled.copiedBuffer("hello,server=" + i, CharsetUtil.UTF_8));
                                    }
                                }

                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                                    System.out.println("client 自定义 handler...");

                                    byte[] info = new byte[msg.readableBytes()];

                                    System.out.println("client 收到的消息：" + new String(info, CharsetUtil.UTF_8));
                                    System.out.println("client 收到的消息数量=" + (++this.count) + "\t");

                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    ctx.close();
                                }
                            });

                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8090).sync();

            channelFuture.channel().closeFuture().sync();


        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }


    }

}
