package com.at._08_netty._16_handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @create 2022-04-27
 */
public class HandlerClient {

    public static void main(String[] args) throws Exception{


        EventLoopGroup group = new NioEventLoopGroup();

        try {

            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            /*

                                other handler -> 编码器 handler
                             */

                            System.out.println("client ChannelInitializer 被调用~~~");

                            ChannelPipeline pipeline = ch.pipeline();

                            //添加出站编码 handler
                            pipeline.addLast(new MessageToByteEncoder<Long>() {
                                @Override
                                protected void encode(ChannelHandlerContext ctx, Long msg, ByteBuf out) throws Exception {

                                    System.out.println("client 端编码器 MessageToByteEncoder 被调用~~~");
                                    System.out.println("client 端发送的数据 msg = " + msg);

                                    //将数据发送到 server 端
                                    out.writeLong(msg);

                                }
                            });

                            //添加自定义处理 handler 处理业务
                            pipeline.addLast(new SimpleChannelInboundHandler<Long>() {

                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {

                                }

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {

                                    System.out.println("client 自定义 handler 被调用~~~");

                                    //数据将交由编码器处理
                                    ctx.writeAndFlush(1243143L);
                                }
                            });



                        }
                    });

            System.out.println("client initializer ok...");

            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8091).sync();


            channelFuture.channel().closeFuture().sync();

        }finally {
            group.shutdownGracefully();
        }



    }

}
