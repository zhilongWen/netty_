package com.at._08_netty._18_protocol_tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @create 2022-04-30
 */
public class ProtocolClient {

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

                            ChannelPipeline pipeline = ch.pipeline();

                            //添加编码器
                            pipeline.addLast(new MessageToByteEncoder<MessageProtocol>() {
                                @Override
                                protected void encode(ChannelHandlerContext ctx, MessageProtocol msg, ByteBuf out) throws Exception {
                                    System.out.println("client 编码器...");
                                    out.writeInt(msg.getLen());
                                    out.writeBytes(msg.getContent());
                                }
                            });

                            //添加解码器
                            pipeline.addLast(new ReplayingDecoder<MessageProtocol>() {
                                @Override
                                protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

                                    System.out.println("client 解码器 ...");

                                    int len = in.readInt();

                                    byte[] content = new byte[len];
                                    in.readBytes(content);

                                    out.add(new MessageProtocol(len,content));

                                }
                            });

                            //添加handler
                            pipeline.addLast(new SimpleChannelInboundHandler<MessageProtocol>() {

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {

                                    for (int i = 0; i < 5; i++) {
                                        String msg = "i am " + i;
                                        byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
                                        int length = msg.getBytes(StandardCharsets.UTF_8).length;

                                        //创建协议对象
                                        MessageProtocol messageProtocol = new MessageProtocol(length, bytes);

                                        ctx.writeAndFlush(messageProtocol);

                                    }

                                }

                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {

                                    System.out.println("client 读取数据 ~~~");

                                    System.out.println("读取数据长度 len=" + msg.getLen());
                                    System.out.println("读取数据内容 content=" + new String(msg.getContent(), Charset.forName("utf-8")));


                                    System.out.println();
                                    System.out.println();

                                }
                            });


                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8090).sync();

            channelFuture.channel().closeFuture().sync();

        }finally {
            group.shutdownGracefully();
        }

    }

}
