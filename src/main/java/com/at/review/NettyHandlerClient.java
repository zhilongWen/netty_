package com.at.review;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;

/**
 * @create 2022-05-09
 */
public class NettyHandlerClient {

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

                            // 添加一个出站的编码器
                            pipeline.addLast(new MessageToByteEncoder<Long>() {
                                @Override
                                protected void encode(ChannelHandlerContext ctx, Long msg, ByteBuf out) throws Exception {

                                    System.out.println("client 出站编码器 MessageToByteEncoder 被调用~~~~");

                                    System.out.println("[MessageToByteEncoder] client 端发送的信息 msg = " + msg);

                                    // 将数据编码后发出
                                    out.writeLong(msg);

                                }
                            });

                            // 添加自定义 handler 在 channel 建立成功时发送数据 并 读取 server 端 回复的信息
                            pipeline.addLast(new SimpleChannelInboundHandler<Long>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {

                                }

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    super.channelActive(ctx);

                                    System.out.println("client 自定义 handler channelActive 被调用~~~~");

//                                    ctx.writeAndFlush(12345678L);


                                    /*
                                        qwertyuiqwertyui 16个字节

                                        server 端的解码器每次处理 8 个字节
                                        所以 server 端编码器被调用了两次 解编码器将数据分两次发送到下游的 handler 所有 下游的 handler 也被调用了两次


                                        client 端的编码器没有被调用？？？？

                                            编码器 MessageToByteEncoder 的 write 方法会判断传入的类型是不是需要编码处理
                                            因此编写 Encoder 是要注意传入的数据类型和处理的数据类型一致

                                            io.netty.handler.codec.MessageToByteEncoder.write
                                            @Override
                                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                                ByteBuf buf = null;
                                                try {
                                                    if (acceptOutboundMessage(msg)) {  //判断当前msg 是不是应该处理的类型，如果是就处理，不是就跳过encode
                                                        @SuppressWarnings("unchecked")
                                                        I cast = (I) msg;
                                                        buf = allocateBuffer(ctx, cast, preferDirect);
                                                        try {
                                                            encode(ctx, cast, buf);
                                                        } finally {
                                                            ReferenceCountUtil.release(cast);
                                                        }

                                                        if (buf.isReadable()) {
                                                            ctx.write(buf, promise);
                                                        } else {
                                                            buf.release();
                                                            ctx.write(Unpooled.EMPTY_BUFFER, promise);
                                                        }
                                                        buf = null;
                                                    } else {
                                                        ctx.write(msg, promise);
                                                    }
                                                } catch (EncoderException e) {
                                                    throw e;
                                                } catch (Throwable e) {
                                                    throw new EncoderException(e);
                                                } finally {
                                                    if (buf != null) {
                                                        buf.release();
                                                    }
                                                }
                                            }

                                     */

                                    ctx.writeAndFlush(Unpooled.copiedBuffer("qwertyuiqwertyui", CharsetUtil.UTF_8));


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
