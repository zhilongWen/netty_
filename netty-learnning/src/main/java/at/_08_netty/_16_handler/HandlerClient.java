package at._08_netty._16_handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;

import java.util.List;

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

                                    System.out.println("client 端 编码器 MessageToByteEncoder 被调用~~~");
                                    System.out.println("client 端发送的数据 msg = " + msg);

                                    //将数据发送到 server 端
                                    out.writeLong(msg);

                                }
                            });

                            //添加解码器
                            pipeline.addLast(new ByteToMessageDecoder() {
                                @Override
                                protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                                    System.out.println("client 端 解码器 ByteToMessageDecoder 被调用~~~");

                                    //因为 long 8个字节, 需要判断有8个字节，才能读取一个long
                                    if(in.readableBytes() >= 8) out.add(in.readLong());
                                }
                            });

                            //添加自定义处理 handler 处理业务
                            pipeline.addLast(new SimpleChannelInboundHandler<Long>() {

                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {

                                    System.out.println("client 读取 server 回复的信息：msg = " + msg);

                                }

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {

                                    System.out.println("client 自定义 handler 被调用~~~");

                                    //数据将交由编码器处理
                                    ctx.writeAndFlush(1243143L);


                                    /*
                                        qwertyuiqwertyui 16个字节

                                        server 端的解码器每次处理 8 个字节
                                        所以 server 端编码器被调用了两次 解编码器将数据分两次发送到下游的 handler 所有 下游的 handler 也被调用了两次


                                        client 端的编码器没有被调用？？？？

                                            编码器 MessageToByteEncoder 的 write 方法会判断传入的类型是不是需要编码处理
                                            因此编写 Encoder 是要注意传入的数据类型和处理的数据类型一致

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
//                                    ctx.writeAndFlush(Unpooled.copiedBuffer("qwertyuiqwertyui", CharsetUtil.UTF_8));


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
