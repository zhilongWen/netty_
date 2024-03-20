package com.at.rpc.server;

import com.at.rpc.MessageFlag;
import com.at.rpc.MessageProtocol;
import com.at.rpc.MessageType;
import io.netty.channel.ChannelHandlerContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public abstract class RpcServerProcess {

    private RpcServer server;

    private RpcServerHandler handler;

    public RpcServer getServer() {
        return server;
    }

    public void setServer(RpcServer server) {
        this.server = server;
    }

    public RpcServerHandler getHandler() {
        return handler;
    }

    public void setHandler(RpcServerHandler handler) {
        this.handler = handler;
    }

    public abstract void process(ChannelHandlerContext ctx, MessageProtocol msg);

    public void send(ChannelHandlerContext ctx, long requestId, MessageType type, MessageFlag flag, byte[] msg) {
        MessageProtocol messageProtocol = new MessageProtocol(type.value, flag.value, msg.length, msg, requestId);
        ctx.writeAndFlush(messageProtocol);
    }

    public void send(ChannelHandlerContext ctx, long requestId, MessageType type, MessageFlag flag, Object msg) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(msg);
            out.flush();

            byte[] result = bos.toByteArray();

            bos.close();

            send(ctx, requestId, type, flag, result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(ChannelHandlerContext ctx, long requestId, Object msg) {
        send(ctx, requestId, MessageType.SERVER, MessageFlag.DATA, msg);
    }

    public void send(ChannelHandlerContext ctx, Object msg) {
        send(ctx, System.nanoTime(), msg);
    }


}
