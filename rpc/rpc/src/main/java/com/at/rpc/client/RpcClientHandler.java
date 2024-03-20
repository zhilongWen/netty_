package com.at.rpc.client;

import com.at.rpc.MessageProtocol;
import com.at.rpc.RpcFuture;
import com.at.rpc.RpcRequestFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class RpcClientHandler extends ChannelInboundHandlerAdapter {

    private final RpcClient client;

    public RpcClientHandler(RpcClient client) {
        this.client = client;
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
}
