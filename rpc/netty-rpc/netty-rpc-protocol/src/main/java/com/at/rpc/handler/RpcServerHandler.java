package com.at.rpc.handler;

import com.at.rpc.constants.ReqType;
import com.at.rpc.core.Header;
import com.at.rpc.core.RpcProtocol;
import com.at.rpc.core.RpcRequest;
import com.at.rpc.core.RpcResponse;
import com.at.rpc.spring.SpringBeansManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RpcServerHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> msg) throws Exception {

        RpcProtocol<RpcResponse> resProtocol = new RpcProtocol<>();

        Header header = msg.getHeader();
        header.setReqType(ReqType.RESPONSE.code());
        Object result = invoke(msg.getContent());

        resProtocol.setHeader(header);
        RpcResponse response = new RpcResponse();
        response.setData(result);
        response.setMsg("success");
        resProtocol.setContent(response);

        ctx.writeAndFlush(resProtocol);

    }

    private Object invoke(RpcRequest request) {
        try {
            Class<?> clazz = Class.forName(request.getClassName());
            Object bean = SpringBeansManager.getBean(clazz); //获取实例对象(CASE)
            Method declaredMethod = clazz.getDeclaredMethod(request.getMethodName(), request.getParameterTypes());
            return declaredMethod.invoke(bean, request.getParams());
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
