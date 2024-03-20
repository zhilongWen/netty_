package com.at.rpc.server;

import com.at.rpc.codec.MessageDecoder;
import com.at.rpc.codec.MessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
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
