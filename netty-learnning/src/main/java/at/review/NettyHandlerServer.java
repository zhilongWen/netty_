package at.review;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @create 2022-05-09
 */
public class NettyHandlerServer {


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
StringDecoder

....


     */
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
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.channel().eventLoop().execute(() -> {});

                            pipeline.channel().eventLoop().schedule(() -> {},10, TimeUnit.SECONDS);

                            // 添加一个入站的解码器
//                            pipeline.addLast(new ByteToMessageDecoder() {
//                                @Override
//                                protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
//
//                                    /*
//                                        decode 方法会根据接收的数据 被调用多次 直到没有新的元素被添加到 out 中 或者 ByteBuf 没有更多的可读字节
//
//                                        如果 out 不为空 就会将 list 传递给下一个 ChannelInboundHandler 处理 该处理器的方法也会被调用多次
//
//                                     */
//
//                                    System.out.println("server 端解码器 ByteToMessageDecoder 被调用~~~~");
//
//                                    // 因为 long 8个字节, 需要判断有8个字节，才能读取一个long
//                                    if (in.readableBytes() >= 8) out.add(in.readLong());
//
//                                }
//                            });


                            pipeline.addLast(new ReplayingDecoder<Void>() {

                                // public abstract class ReplayingDecoder<S> extends ByteToMessageDecoder

                                @Override
                                protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

                                    System.out.println("server 端解码器 ReplayingDecoder 被调用~~~~");

                                    // ReplayingDecoder 无需判断数据是否足够读取，内部会进行判断
                                    out.add(in.readLong());

                                }
                            });

                            // 添加自定义 handler
                            pipeline.addLast(new SimpleChannelInboundHandler<Long>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {

                                    System.out.println("自定义 handler SimpleChannelInboundHandler 被调用。。。。");

                                    System.out.println("从 client：" + ctx.channel().remoteAddress() + " 读取到数据 msg = " + msg);

                                    // 回复一条信息

                                }

                                @Override
                                public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
                                    super.handlerAdded(ctx);
                                    System.out.println("channel：" + ctx.channel().hashCode() + " 加入 pipline：" + ctx.pipeline().hashCode());
                                }

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    super.channelActive(ctx);
                                    System.out.println("channel：" + ctx.channel().hashCode() + " 成功建立连接 ");
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    super.exceptionCaught(ctx, cause);
                                    ctx.close();
                                }


                            });


                        }
                    });

            ChannelFuture channelFuture = serverBootstrap.bind("127.0.0.1", 8090).sync();

            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) System.out.println("server 监听 8090 端口成功");
                    else System.out.println("server 监听 8090 端口失败");
                }
            });

            channelFuture.channel().closeFuture().sync();


        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }


    }

}
