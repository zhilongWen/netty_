package com.at._08_netty._16_handler;

import com.sun.corba.se.internal.Interceptors.PIORB;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.List;

/**
 * @create 2022-04-27
 */
public class HandlerServer {

    /*

                        出栈
        client  -------------------->   server
                <--------------------
                        入栈






        client                                                                                                                    server
                                      +--------------------------+                    +--------------------------+
                    +---------------- | decoder (inBoundHandler) | <---+   +--------- | encoder(outBoundHandler) | <------------+
                    |                 +--------------------------+     |   |          +--------------------------+              |
                    |                                                  |   |                                                    |
                    ↓                                                  |   ↓                                                    |
            +---------------+                                        +--------+                                           +---------------+
            | clientHandler |                                        | socket |                                           | serverHandler |
            +---------------+                                        +--------+                                           +---------------+
                    |                                                  ↑   |                                                    ↑
                    |                                                  |   |                                                    |
                    |                  +--------------------------+    |   |            +--------------------------+            |
                    +----------------> | encoder(outBoundHandler) | ---+   +----------> | decoder (inBoundHandler) | -----------+
                                       +--------------------------+                     +--------------------------+




        不论解码器handler 还是 编码器handler 即接  收的消息类型必须与待处理的消息类型一致， 否则该handler不会被执行
        在解码器 进行数据解码时，需要判断 缓存 区	的数据是否足够 ，否则接收到的结果会与期望的结果不一致


LineBasedFrameDecoder：这个类在Netty内部也有使用，它使用行尾控制字符（\n或者\r\n） 作为分隔符来解析数据。
DelimiterBasedFrameDecoder：使用自定义 的特殊字符作为消息的分隔符。
HttpObjectDecoder：一个HTTP数据的解码器
LengthFieldBasedFrameDecoder：通过指定 长度来标识整包消息，这样就可以自动的处理 黏包和半包消息。

....


     */

    public static void main(String[] args) throws Exception {


        EventLoopGroup boosGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup(4);


        try {

            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .group(boosGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 64)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            /*

                                解码器 handler -> other handler

                             */

                            System.out.println("server ChannelInitializer 被调用~~~");

                            ChannelPipeline pipeline = ch.pipeline();

                            //添加一个入栈的解码器 MessageToByteEncoder

/*
                            pipeline.addLast(new ByteToMessageDecoder() {
                                */
/**
                                 *  decode 方法会根据接收的数据 被调用多次 直到没有新的元素被添加到 list 中
                                 *  或者 ByteBuf 没有更多的可读字节
                                 *
                                 *  如果 list 不为空 就会将 list 传递给下一个 ChannelInboundHandler 处理 该处理器的方法也会被调用多次
                                 *
                                 * @param ctx
                                 * @param in 入站的 ByteBuf
                                 * @param out List 集合，将解码后的数据传给下一个handler
                                 * @throws Exception
                                 *//*

                                @Override
                                protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

                                    System.out.println("server 端解码器 ByteToMessageDecoder 被调用~~~");

                                    //因为 long 8个字节, 需要判断有8个字节，才能读取一个long
                                    if(in.readableBytes() >= 8) out.add(in.readLong());

                                }
                            });
*/


                            pipeline.addLast(new ReplayingDecoder<Void>() {
                                @Override
                                protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

                                    System.out.println("server 端解码器 ReplayingDecoder 被调用~~~");

                                    //ReplayingDecoder 无需判断数据是否足够读取，内部会进行判断
                                    out.add(in.readLong());

                                }
                            });





                            //添加编码器
                            pipeline.addLast(new MessageToByteEncoder<Long>() {
                                @Override
                                protected void encode(ChannelHandlerContext ctx, Long msg, ByteBuf out) throws Exception {
                                    System.out.println("server 端编码器 MessageToByteEncoder 被调用~~~");
                                    System.out.println("server 端发送的数据 msg = " + msg);

                                    //将数据发送到 server 端
                                    out.writeLong(msg);
                                }
                            });

                            //添加自定义处理handler 处理client中的数据
                            pipeline.addLast(new SimpleChannelInboundHandler<Long>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {

                                    System.out.println("server 自定义处理handler~~~");

                                    System.out.println("从 client：" + ctx.channel().remoteAddress() + " 读取到数据 = " + msg);


                                    //处理完数据后给 client 端发送一条数据
                                    ctx.writeAndFlush(12345L);

                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    cause.printStackTrace();
                                    ctx.close();
                                }
                            });



                        }
                    });


            System.out.println("server initializer ok...");

            ChannelFuture channelFuture = serverBootstrap.bind(8091).sync();

            channelFuture.addListener(future -> {
                if (future.isSuccess()) System.out.println("server 监听 8090 端口成功");
                else System.out.println("server 监听 8090 s端口失败");
            });

            channelFuture.channel().closeFuture().sync();

        } finally {
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }


    }

}
