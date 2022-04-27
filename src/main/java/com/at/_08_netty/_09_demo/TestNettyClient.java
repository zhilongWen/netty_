package com.at._08_netty._09_demo;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

import java.util.Optional;

/**
 * @create 2022-04-21
 */
public class TestNettyClient {

    public static void main(String[] args) {

        //客户端需要一个事件循环组
        EventLoopGroup eventExecutors = null;

        try {
            eventExecutors = new NioEventLoopGroup();

            //创建客户端启动对象
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(eventExecutors)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

//                            ch.pipeline().addLast(new TestClientHandler());

                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {

                                    System.out.println("client ChannelHandlerContext：" + ctx);
                                    ctx.writeAndFlush(Unpooled.copiedBuffer("client 端通道已就绪...", CharsetUtil.UTF_8));

                                }


                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

                                    ByteBuf info = (ByteBuf) msg;
                                    System.out.println("client端收到server端回复的信息：" + info.toString(CharsetUtil.UTF_8));
                                    System.out.println("服务器的地址：" + ctx.channel().remoteAddress());

                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    cause.printStackTrace();
                                    ctx.channel().close();
                                }
                            });

                        }
                    });

            System.out.println("client 端已 ok ...");

            //启动客户端去连接服务器端
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 9089);

            channelFuture.channel().closeFuture().sync();


        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

            Optional.ofNullable(eventExecutors).ifPresent(f -> f.shutdownGracefully());

        }


    }

}
