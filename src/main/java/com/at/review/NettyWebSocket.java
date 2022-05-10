package com.at.review;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @create 2022-05-10
 */
public class NettyWebSocket {

    public static void main(String[] args) {

        EventLoopGroup boosGroup = null;
        EventLoopGroup workGroup = null;

        try {
            boosGroup = new NioEventLoopGroup(1);
            workGroup = new NioEventLoopGroup(4);

            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .group(boosGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 64)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ChannelPipeline pipeline = ch.pipeline();

                            // http 编解码器
                            pipeline.addLast(new HttpServerCodec());

                            // 是以块方式写，添加ChunkedWriteHandler处理器
                            pipeline.addLast(new ChunkedWriteHandler());

                            /*
                                http数据在传输过程中是分段, HttpObjectAggregator ，就是可以将多个段聚合
                                这就就是为什么，当浏览器发送大量数据时，就会发出多次http请求
                             */
//                            pipeline.addLast(new HttpObjectAggregator(8192));
                            pipeline.addLast(new HttpObjectAggregator(5));

                            /*
                                1. 对应websocket ，它的数据是以 帧(frame) 形式传递
                                2. 可以看到WebSocketFrame 下面有六个子类
                                3. 浏览器请求时 ws://localhost:7000/hello 表示请求的uri
                                4. WebSocketServerProtocolHandler 核心功能是将 http协议升级为 ws协议 , 保持长连接
                                5. 是通过一个 状态码 101
                             */
                            pipeline.addLast(new WebSocketServerProtocolHandler("/hello"));

                            // 自定义的handler ，处理业务逻辑   TextWebSocketFrame 类型，表示一个文本帧(frame)
                            pipeline.addLast(new SimpleChannelInboundHandler<TextWebSocketFrame>() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    super.channelActive(ctx);
                                    System.out.println(ctx.channel().id().asLongText() + " 通道建立连接。。。");
                                }

                                @Override
                                public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                    super.channelInactive(ctx);
                                    System.out.println(ctx.channel().id().asLongText() + " 通道被移除。。。");
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    System.out.println("异常：" + cause.getMessage());
                                    ctx.close();
                                }

                                @Override
                                public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
                                    super.handlerAdded(ctx);

                                    // id 表示唯一的值，LongText 是唯一的 ShortText 不是唯一
                                    System.out.println("handlerAdded 被调用 asLongText ：" + ctx.channel().id().asLongText());
                                    System.out.println("handlerAdded 被调用 asShortText ：" + ctx.channel().id().asShortText());

                                }

                                @Override
                                public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
                                    super.handlerRemoved(ctx);
                                    System.out.println("handlerRemoved 被调用：" + ctx.channel().id().asLongText());
                                }

                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

                                    System.out.println("server 接收到的数据;" + msg.text());

                                    //回复消息给client
                                    ctx.channel().writeAndFlush(new TextWebSocketFrame("server 端 time：" + LocalDateTime.now() + " " + msg.text()));

                                }
                            });

                        }
                    });

            ChannelFuture channelFuture = serverBootstrap.bind("127.0.0.1", 8090).sync();

            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) System.out.println("8090 ok...");
                    else System.out.println("8090 fail...");
                }
            });

            channelFuture.channel().closeFuture().sync();


        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            Optional.ofNullable(boosGroup).ifPresent(b -> b.shutdownGracefully());
            Optional.ofNullable(workGroup).ifPresent(w -> w.shutdownGracefully());
        }

    }

}
