package at._08_netty._19;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;

/**
 * @create 2023-06-05
 */
public class ExceptionServer {

    public static void main(String[] args) {

        NioEventLoopGroup boosGroup = null;
        NioEventLoopGroup workGroup = null;


        try {

            boosGroup = new NioEventLoopGroup(1);
            workGroup = new NioEventLoopGroup();

            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap
                    .group(boosGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.SO_BACKLOG, 8)
                    .childAttr(AttributeKey.newInstance("abc"), "abc")
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(
                            new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel ch) throws Exception {

                                    ChannelPipeline pipeline = ch.pipeline();

                                    pipeline
                                            .addLast(new ExceptionInBoundHandlerA())
                                            .addLast(new ExceptionInBoundHandlerB())
                                            .addLast(new ExceptionInBoundHandlerC())
                                            .addLast(new ExceptionOutBoundHandlerA())
                                            .addLast(new ExceptionOutBoundHandlerB())
                                            .addLast(new ExceptionOutBoundHandlerC());

                                    pipeline.addLast(new ExceptionCaughtHandler());

//                                    InBoundHandlerB.exceptionCaught()
//                                    InBoundHandlerC.exceptionCaught()
//                                    OutBoundHandlerA.exceptionCaught()
//                                    OutBoundHandlerB.exceptionCaught()
//                                    OutBoundHandlerC.exceptionCaught()

                                }
                            }
                    );


            ChannelFuture channelFuture = serverBootstrap.bind("127.0.0.1", 9099).sync();

            channelFuture.channel().closeFuture().sync();


        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }


    }

}

class ExceptionCaughtHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // ..

        if (cause instanceof BusinessException) {
            System.out.println("BusinessException");
        }
    }
}



class ExceptionInBoundHandlerA extends ChannelInboundHandlerAdapter {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("InBoundHandlerA.exceptionCaught()");

        ctx.fireExceptionCaught(cause);
    }
}


class ExceptionInBoundHandlerB extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        throw new BusinessException("from InBoundHandlerB");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("InBoundHandlerB.exceptionCaught()");

        ctx.fireExceptionCaught(cause);
    }
}

class ExceptionInBoundHandlerC extends ChannelInboundHandlerAdapter {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("InBoundHandlerC.exceptionCaught()");

        ctx.fireExceptionCaught(cause);
    }
}


class ExceptionOutBoundHandlerA extends ChannelOutboundHandlerAdapter {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("OutBoundHandlerA.exceptionCaught()");
        ctx.fireExceptionCaught(cause);
    }
}


class ExceptionOutBoundHandlerB extends ChannelOutboundHandlerAdapter {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("OutBoundHandlerB.exceptionCaught()");
        ctx.fireExceptionCaught(cause);
    }
}

class ExceptionOutBoundHandlerC extends ChannelOutboundHandlerAdapter {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("OutBoundHandlerC.exceptionCaught()");
        ctx.fireExceptionCaught(cause);
    }
}












