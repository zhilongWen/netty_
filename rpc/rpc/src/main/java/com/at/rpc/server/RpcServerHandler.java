package com.at.rpc.server;

import com.at.rpc.MessageProtocol;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class RpcServerHandler extends ChannelInboundHandlerAdapter {

    private final RpcServer server;

    public RpcServerHandler(RpcServer server) {
        this.server = server;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        MessageProtocol messageProtocol = (MessageProtocol) msg;

        try {
            server.getProcess().process(ctx, messageProtocol);
        } finally {
            messageProtocol.release();
        }

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        System.out.println("server channelActive");
//
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        ObjectOutputStream out = new ObjectOutputStream(bos);
//        out.writeObject("server msg");
//        out.flush();
//
//        byte[] result = bos.toByteArray();
//
//        bos.close();
//
//
//        MessageProtocol messageProtocol = new MessageProtocol((byte) 0, (byte) 0xC, result.length, result, 11L);
//        ctx.writeAndFlush(messageProtocol);

    }
}
