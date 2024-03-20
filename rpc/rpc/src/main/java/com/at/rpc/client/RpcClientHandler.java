package com.at.rpc.client;

import com.at.rpc.*;
import com.at.rpc.utils.ThreadUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateHandler;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
public class RpcClientHandler extends ChannelInboundHandlerAdapter {

    private final RpcClient client;

    private String channelId;

    private ScheduledExecutorService hearbeatExecutor = ThreadUtils.newDaemonSingleThreadScheduledExecutor("hearbeat");

    public RpcClientHandler(RpcClient client) {
        this.client = client;
        channelId = this.hashCode() + "";
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        MessageProtocol messageProtocol = (MessageProtocol) msg;

        try {
            client.getProcess().process(ctx, messageProtocol);
            RpcFuture<MessageProtocol> future = RpcRequestFuture.SYNC_WRITE_MAP.remove(messageProtocol.getRequestId());
            if (future != null) {
                future.getPromise().setSuccess(messageProtocol);
            }
        } finally {
            messageProtocol.release();
        }


    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        hearbeatExecutor.scheduleAtFixedRate(() -> {
            ctx.writeAndFlush(new MessageProtocol(MessageType.CLIENT.value, MessageFlag.HEARBEAT.value, channelId.length(), channelId.getBytes(StandardCharsets.UTF_8), System.nanoTime()));
        }, 0, 3, TimeUnit.SECONDS);

        System.out.println("server channelActive");
//
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        ObjectOutputStream out = new ObjectOutputStream(bos);
//        out.writeObject("client msg");
//        out.flush();
//
//        byte[] result = bos.toByteArray();
//
//        bos.close();
//
//
//        MessageProtocol messageProtocol = new MessageProtocol((byte) 0, (byte) 0xC, result.length, result, 1L);
//        ctx.writeAndFlush(messageProtocol);


    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }
}
