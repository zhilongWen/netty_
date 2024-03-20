package com.at.rpc.client;

import com.at.rpc.codec.MessageDecoder;
import com.at.rpc.codec.MessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class RpcClient {

    private final String host;

    private final int port;

    NioEventLoopGroup group;

    private RpcClientProcess process;

    private RpcClientHandler handler;

    private Channel channel;

    public RpcClientProcess getProcess() {
        return process;
    }

    public Channel getChannel() {
        return channel;
    }

    public RpcClient(String host, int port, RpcClientProcess process) throws InterruptedException {
        this.host = host;
        this.port = port;

        this.process = process;
        this.handler = new RpcClientHandler(this);

        this.process.setClient(this);
        this.process.setHandler(handler);


        group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();

        bootstrap
                .group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline
                                .addLast(new MessageDecoder())
                                .addLast(new MessageEncoder())
                                .addLast(handler);
                    }
                });

        start(bootstrap);

    }

    private void start(Bootstrap bootstrap) throws InterruptedException {
        ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
        System.out.println("client start...");
        this.channel = channelFuture.channel();
//        this.channel.closeFuture().sync();
    }

    public void close() {
        System.out.println("close client...");
        channel.close();
        group.shutdownGracefully();
    }

}
