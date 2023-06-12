package com.at.netsupervisor.netty.server;

import com.alibaba.fastjson.JSON;
import com.at.netsupervisor.entity.Request;
import com.at.netsupervisor.entity.Response;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @create 2023-06-12
 */
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private final Map<String, Object> serviceMap;

    public NettyServerHandler(Map<String, Object> serviceMap) {
        this.serviceMap = serviceMap;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("客户端连接成功!"+ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("客户端断开连接!{}",ctx.channel().remoteAddress());
        ctx.channel().close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        Request request = JSON.parseObject(msg.toString(), Request.class);


        if ("heartBeat".equals(request.getMethodName())){
            logger.info("客户端心跳信息..."+ctx.channel().remoteAddress());
        }else {

            logger.info("RPC客户端请求接口:"+request.getClassName()+"   方法名:"+request.getMethodName());

            Response response = new Response();
            response.setRequestId(response.getRequestId());

            try {
                Object result = handler(request);
                response.setData(result);
            }catch (Exception e){
                e.printStackTrace();
                response.setCode(1);
                response.setError_msg(e.toString());
                logger.error("RPC Server handle request error",e);
            }

            ctx.writeAndFlush(request);

        }


    }


    /**
     * 通过反射，执行本地方法
     * @param request
     * @return
     * @throws Throwable
     */
    private Object handler(Request request) throws Exception {

        String className = request.getClassName();
        Object serverBean = serviceMap.get(className);

        if (serverBean != null){

            Class<?> serverClass = serverBean.getClass();
            String methodName = request.getMethodName();
            Class<?>[] parameterTypes = request.getParameterTypes();
            Object[] parameters = request.getParameters();

            Method method = serverClass.getMethod(methodName, parameterTypes);
            method.setAccessible(true);
            Object invoke = method.invoke(serverBean, getParameters(parameterTypes, parameters));

            return invoke;

        }

        throw new Exception("未找到服务接口,请检查配置!:"+className+"#"+request.getMethodName());


    }

    /**
     * 获取参数列表
     * @param parameterTypes
     * @param parameters
     * @return
     */
    private Object[] getParameters(Class<?>[] parameterTypes,Object[] parameters){

        if (parameters == null && parameters.length == 0){
            return parameters;
        }

        Object[] newParams = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            newParams[i] = JSON.parseObject(parameters[i].toString(),parameterTypes[i]);
        }

        return newParams;

    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE){
                logger.info("客户端已超过60秒未读写数据,关闭连接.{}",ctx.channel().remoteAddress());
                ctx.channel().close();
            }
        }else {
            super.userEventTriggered(ctx,evt);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info(cause.getMessage());
        ctx.close();
    }
}
