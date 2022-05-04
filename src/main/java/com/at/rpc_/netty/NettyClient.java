package com.at.rpc_.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @create 2022-05-03
 */
public class NettyClient {

    private static ExecutorService executor = Executors.newFixedThreadPool(2);

    private static NettyClientHandler handler;

    public  Object getBean(final Class<?> serivceClass) {


//        Object instance = Proxy.newProxyInstance(
//                Thread.currentThread().getContextClassLoader(),
//                new Class<?>[]{serverClass},
//                new InvocationHandler() {
//                    @Override
//                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//
//
//                        System.out.println("invoke_1 handler = " + handler);
//
//                        if(handler == null) initClient();
//
//                        System.out.println("invoke_2 handler = " + handler);
//
//                        handler.setParam("rpc_");
//
//                        Object result = executor.submit(handler).get();
//
//                        return result;
//                    }
//                }
//        );
//
//        System.out.println("instance = " + instance);
//
//        return instance;

        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{serivceClass}, (proxy, method, args) -> {

                    System.out.println("Proxy.newProxyInstance thread = " + Thread.currentThread().getName());

                    //{}  部分的代码，客户端每调用一次 hello, 就会进入到该代码
                    if (handler == null) {
                        initClient();
                    }

                    //设置要发给服务器端的信息
                    //providerName 协议头 args[0] 就是客户端调用api hello(???), 参数
                    handler.setParam("rpc_" + args[0]);

                    //
                    return executor.submit(handler).get();

                });

    }


//    private static void initClient() {

    private static void initClient() {
        handler = new NettyClientHandler();

        EventLoopGroup group = new NioEventLoopGroup(2);

        try {
            Bootstrap b = new Bootstrap();

            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(new StringEncoder());

                            pipeline.addLast(handler);

                        }
                    });

            ChannelFuture channelFuture = b.connect("127.0.0.1", 8090).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }


    }

}
