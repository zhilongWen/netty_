package com.at.rpc.server;

import com.at.rpc.codec.MessageDecoder;
import com.at.rpc.codec.MessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.commons.lang.StringUtils;

import java.net.InetSocketAddress;

public class RpcServer {

    private final String host;

    private final int port;

    NioEventLoopGroup workGroup;
    NioEventLoopGroup bossGroup;

    Channel channel;

    private RpcServerHandler handler;

    private RpcServerProcess process;

    public RpcServerProcess getProcess() {
        return process;
    }

    public RpcServer(String host, int port, RpcServerProcess process) {

        this.host = host;
        this.port = port;

        this.process = process;
        this.handler = new RpcServerHandler(this);

        this.process.setServer(this);
        this.process.setHandler(handler);

        workGroup = new NioEventLoopGroup(1);
        bossGroup = new NioEventLoopGroup();

    }

    public void start() {

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap
                    .group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 64)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline
                                    .addLast(new MessageEncoder())
                                    .addLast(new MessageDecoder())
                                    /*
                                    1. IdleStateHandler 是netty 提供的处理空闲状态的处理器
                                    2. long readerIdleTime : 表示多长时间没有读, 就会发送一个心跳检测包检测是否连接
                                    3. long writerIdleTime : 表示多长时间没有写, 就会发送一个心跳检测包检测是否连接
                                    4. long allIdleTime : 表示多长时间没有读写, 就会发送一个心跳检测包检测是否连接
                                    5. 文档说明
                                    triggers an {@link IdleStateEvent} when a {@link Channel} has not performed read, write, or both operation for a while.
                                    6. 当 IdleStateEvent 触发后 , 就会传递给管道 的下一个handler去处理通过调用(触发)下一个handler 的 userEventTiggered , 在该方法中去处理 IdleStateEvent(读空闲，写空闲，读写空闲)
                                    */
                                    .addLast(new IdleStateHandler(0, 0, 120))
                                    .addLast(handler);

                        }
                    });

            InetSocketAddress localAddress = StringUtils.isBlank(host) ? new InetSocketAddress(port) : new InetSocketAddress(host, port);
            ChannelFuture channelFuture = serverBootstrap.bind(localAddress).sync();

            System.out.println("server start...");

            channel = channelFuture.channel();

            channel.closeFuture().sync();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        System.out.println("close server...");
        workGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }

}
