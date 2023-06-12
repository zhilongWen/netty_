package com.at.netsupervisor.netty.server;

import com.at.netsupervisor.annotation.RpcService;
import com.at.netsupervisor.netty.codec.JSONDecoder;
import com.at.netsupervisor.netty.codec.JSONEncoder;
import com.at.netsupervisor.registry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * @create 2023-06-11
 */
public class NettyServer implements ApplicationContextAware, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private static final EventLoopGroup boosGroup = new NioEventLoopGroup(1);
    private static final EventLoopGroup workGroup = new NioEventLoopGroup(4);

    private Map<String, Object> serviceMap = new HashMap<>();

    @Value("${rpc.server.address}")
    private String serverAddress;

    @Autowired
    ServiceRegistry registry;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(RpcService.class);

        for (Object serverBean : beans.values()) {

            Class<?> clazz = serverBean.getClass();

            Class<?>[] interfaces = clazz.getInterfaces();

            for (Class<?> anInterface : interfaces) {

                String interfaceName = anInterface.getName();

                logger.info("加载服务类：{}", interfaces);

                serviceMap.put(interfaceName, serverBean);

            }

        }

        logger.info("已加载全部服务接口:{}", serviceMap);

    }


    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }


    public void start() {

        final NettyServerHandler handler = new NettyServerHandler(serviceMap);

        new Thread(() -> {

            try {

                ServerBootstrap serverBootstrap = new ServerBootstrap();

                serverBootstrap.group(boosGroup,workGroup)
                        .channel(NioServerSocketChannel.class)
                        .option(ChannelOption.SO_BACKLOG,1024)
                        .childOption(ChannelOption.SO_KEEPALIVE,true)
                        .childOption(ChannelOption.TCP_NODELAY,true)
                        .handler(new LoggingHandler(LogLevel.INFO))
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {

                                //创建NIOSocketChannel成功后，在进行初始化时，将它的ChannelHandler设置到ChannelPipeline中，用于处理网络IO事件

                                ChannelPipeline pipeline = ch.pipeline();

                                pipeline
                                        .addLast(new IdleStateHandler(0,0,60))
                                        .addLast(new JSONEncoder())
                                        .addLast(new JSONDecoder())
                                        .addLast(handler);

                            }
                        });

                String[] addressArr = serverAddress.split(":");
                String host = addressArr[0];
                int port = Integer.parseInt(addressArr[1]);

                ChannelFuture channelFuture = serverBootstrap.bind(host, port).sync();

                logger.info("RPC 服务器启动.监听端口:"+port);

                registry.register(serverAddress);

                //等待服务端监听端口关闭
                channelFuture.channel().closeFuture().sync();

            }catch (Exception e){
                e.printStackTrace();
                boosGroup.shutdownGracefully();
                workGroup.shutdownGracefully();
            }






        }).start();

    }

}
