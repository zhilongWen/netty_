package com.at.rpc.nio;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class ChannelHandler {

    private SocketChannel channel;
    private Charset charset;

    public ChannelHandler(SocketChannel channel, Charset charset) {
        this.channel = channel;
        this.charset = charset;
    }

    public SocketChannel channel() {
        return channel;
    }

    public void writeAndFlush(Object msg) {

        try {
            byte[] res = msg.toString().getBytes(charset);
            ByteBuffer buffer = ByteBuffer.allocate(res.length);
            buffer.put(res);
            buffer.flip();
            channel.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
