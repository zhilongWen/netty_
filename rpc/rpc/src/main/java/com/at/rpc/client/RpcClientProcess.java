package com.at.rpc.client;

import com.at.rpc.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;

import java.io.*;
import java.util.concurrent.ExecutionException;

public abstract class RpcClientProcess {

    private RpcClient client;

    private RpcClientHandler handler;

    public void setClient(RpcClient client) {
        this.client = client;
    }

    public RpcClient getClient() {
        return client;
    }

    public RpcClientHandler getHandler() {
        return handler;
    }

    public void setHandler(RpcClientHandler handler) {
        this.handler = handler;
    }

    public abstract void process(ChannelHandlerContext ctx, MessageProtocol msg);

    public <T> T send(boolean sync, long requestId, MessageType type, MessageFlag flag, byte[] msg) {

        MessageProtocol messageProtocol = new MessageProtocol(type.value, flag.value, msg.length, msg, requestId);

        if (sync) {
            try {
                client.getChannel().writeAndFlush(messageProtocol);
                RpcFuture<MessageProtocol> future = new RpcFuture<>(new DefaultPromise<>(new DefaultEventLoop()));
                RpcRequestFuture.SYNC_WRITE_MAP.put(requestId, future);

                messageProtocol = future.getPromise().get();
//                try {
//                    messageProtocol = future.getPromise().get(5, TimeUnit.SECONDS);
//                } catch (TimeoutException e) {
//                    throw new RuntimeException(e);
//                }

                ByteArrayInputStream bis = new ByteArrayInputStream(messageProtocol.getContent());
                ObjectInputStream in = new ObjectInputStream(bis);

                T result = (T) in.readObject();

                in.close();
                bis.close();

                return result;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            client.getChannel().writeAndFlush(messageProtocol);
            return null;
        }
    }


    public <T> T send(boolean sync, long requestId, MessageType type, MessageFlag flag, Object msg) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;

        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(msg);
            out.flush();

            byte[] result = bos.toByteArray();

            bos.close();

            return send(sync, requestId, type, flag, result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(long requestId, Object msg) {
        send(false, requestId, MessageType.CLIENT, MessageFlag.DATA, msg);
    }

    public void send(Object msg) {
        send(System.nanoTime(), msg);
    }

    public <T> T ask(long requestId, MessageType type, MessageFlag flag, Object msg) {
        return send(true, requestId, type, flag, msg);
    }

    public <T> T ask(long requestId, Object msg) {
        return ask(requestId, MessageType.CLIENT, MessageFlag.DATA, msg);
    }

}
