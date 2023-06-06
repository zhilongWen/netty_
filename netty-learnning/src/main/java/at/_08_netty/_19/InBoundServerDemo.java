package at._08_netty._19;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.AttributeKey;

import java.util.concurrent.TimeUnit;

/**
 * @create 2023-06-04
 */
public class InBoundServerDemo {


    public static void main(String[] args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childAttr(AttributeKey.newInstance("childAttr"), "childAttrValue")
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {

//                            ch.pipeline().addLast(new InBoundHandlerA());
//                            ch.pipeline().addLast(new InBoundHandlerB());
//                            ch.pipeline().addLast(new InBoundHandlerC());

//                            InBoundHandlerA: hello world
//                            InBoundHandlerB: hello world
//                            InBoundHandlerC: hello world


//                            ch.pipeline().addLast(new InBoundHandlerA());
//                            ch.pipeline().addLast(new InBoundHandlerC());
//                            ch.pipeline().addLast(new InBoundHandlerB());

//                            InBoundHandlerA: hello world
//                            InBoundHandlerC: hello world
//                            InBoundHandlerB: hello world

                            // 事件的传播与添加 handler 的顺序有关


                            ch.pipeline().addLast(new OutBoundHandlerA());
                            ch.pipeline().addLast(new OutBoundHandlerB());
                            ch.pipeline().addLast(new OutBoundHandlerC());

//                            OutBoundHandlerC: hello world
//                            OutBoundHandlerB: hello world
//                            OutBoundHandlerA: hello world

                        }
                    });

            ChannelFuture f = b.bind(8888).sync();

            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}


/*   outbound */
class OutBoundHandlerA extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("OutBoundHandlerA: " + msg);
        ctx.write(msg, promise);
    }
}

class OutBoundHandlerB extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("OutBoundHandlerB: " + msg);
        ctx.write(msg, promise);
    }


    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) {
        ctx.executor().schedule(() -> {
            ctx.channel().write("hello world"); // 从 tail 节点开始传播 tail -> C -> B -> A -> Head
//            ctx.write("hello world"); // 从当前节点开始传播
        }, 3, TimeUnit.SECONDS);
    }
}

class OutBoundHandlerC extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("OutBoundHandlerC: " + msg);
        ctx.write(msg, promise);
//        throw new BusinessException("from OutBoundHandlerC");
    }
}


class BusinessException extends Exception {

    public BusinessException(String message) {
        super(message);
    }
}


/*  inbound */

class InBoundHandlerA extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("InBoundHandlerA: " + msg);
        ctx.fireChannelRead(msg);
    }
}

class InBoundHandlerB extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("InBoundHandlerB: " + msg);

        // 从当前节点向下传播
        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {

        // 这里的 fireChannelRead 从 head 节点向下传播
        ctx.channel().pipeline().fireChannelRead("hello world");
    }
}

class InBoundHandlerC extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("InBoundHandlerC: " + msg);
        ctx.fireChannelRead(msg);
    }
}
