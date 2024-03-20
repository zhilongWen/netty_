package com.at.rpc.server;

import com.at.rpc.MessageFlag;
import com.at.rpc.MessageProtocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class RpcServerHandler extends ChannelInboundHandlerAdapter {

    public static final Logger logger = LoggerFactory.getLogger(RpcServerHandler.class);

    private final RpcServer server;

    public RpcServerHandler(RpcServer server) {
        this.server = server;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        MessageProtocol messageProtocol = (MessageProtocol) msg;

        if (messageProtocol.getFlag() == MessageFlag.HEARBEAT.value) {
//            System.out.println("receive client hearbeat " + msg);
            return;
        }

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

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            synchronized (this) {
                if (e.state() == IdleState.ALL_IDLE) {
                    String address = getRemoteAddress(ctx.channel());
                    logger.error("Connection to {} hash been quiet for {} ms while there are outstanding " +
                            "requests. Assuming connection is dead.", address, 120 * 1000 * 1000);
                    System.out.println("address = " + address + " channel close.");
                    ctx.close();
                }
            }
        }


        ctx.fireUserEventTriggered(evt);
    }

    private String getRemoteAddress(Channel channel) {
        if (channel != null && channel.remoteAddress() != null) {
            return channel.remoteAddress().toString();
        }
        return "<unknown remote>";
    }
}
