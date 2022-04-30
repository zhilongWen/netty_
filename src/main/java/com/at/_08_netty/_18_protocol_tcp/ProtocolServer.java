package com.at._08_netty._18_protocol_tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

/**
 * @create 2022-04-30
 */
public class ProtocolServer {

    public static void main(String[] args) throws Exception {

        EventLoopGroup boosGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup(4);


        try {

            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .group(boosGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 32)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ChannelPipeline pipeline = ch.pipeline();

                            //添加解码器
                            pipeline.addLast(new ReplayingDecoder<MessageProtocol>() {
                                @Override
                                protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

                                    System.out.println("server 解码器 ~~~");

                                    int len = in.readInt();

                                    byte[] content = new byte[len];
                                    in.readBytes(content);

                                    //封装成 MessageProtocol 对象，放入 out， 传递下一个handler业务处理
                                    MessageProtocol messageProtocol = new MessageProtocol(len, content);

                                    out.add(messageProtocol);

                                }
                            });

                            //添加编码器
                            pipeline.addLast(new MessageToByteEncoder<MessageProtocol>() {
                                @Override
                                protected void encode(ChannelHandlerContext ctx, MessageProtocol msg, ByteBuf out) throws Exception {

                                    System.out.println("server 编码器 ~~~");

                                    int len = msg.getLen();
                                    byte[] content = msg.getContent();

                                    out.writeInt(len);
                                    out.writeBytes(content);

                                }
                            });


                            //添加自定义handler
                            pipeline.addLast(new SimpleChannelInboundHandler<MessageProtocol>() {

                                private int count;

                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {

                                    System.out.println("server 接收数据 ~~~");

                                    System.out.println("数据长度 len=" + msg.getLen());
                                    System.out.println("数据内容 content=" + new String(msg.getContent(), Charset.forName("utf-8")));

                                    System.out.println("server 端数据包的数量 count=" + (++this.count));

                                    System.out.println();
                                    System.out.println();


//                                    //回复 client 信息
//                                    String responseMsg = UUID.randomUUID().toString();
//
//                                    byte[] responseContent = responseMsg.getBytes(StandardCharsets.UTF_8);
//                                    int length = responseMsg.getBytes(StandardCharsets.UTF_8).length;
//
//                                    MessageProtocol messageProtocol = new MessageProtocol(length, responseContent);
//
//                                    ctx.writeAndFlush(messageProtocol);



                                }

                                @Override
                                public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

                                    //回复 client 信息
                                    String responseMsg = UUID.randomUUID().toString();

                                    byte[] responseContent = responseMsg.getBytes(StandardCharsets.UTF_8);
                                    int length = responseMsg.getBytes(StandardCharsets.UTF_8).length;

                                    MessageProtocol messageProtocol = new MessageProtocol(length, responseContent);

                                    ctx.writeAndFlush(messageProtocol);
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    super.exceptionCaught(ctx, cause);
                                    ctx.close();
                                }
                            });

                        }
                    });

            ChannelFuture channelFuture = serverBootstrap.bind(8090).sync();

            channelFuture.channel().closeFuture().sync();


        }finally {

            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();

        }

    }

}
