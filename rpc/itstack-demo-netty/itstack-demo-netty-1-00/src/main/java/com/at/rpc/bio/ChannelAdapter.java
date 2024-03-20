package com.at.rpc.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.Charset;

public abstract class ChannelAdapter extends Thread {

    private Socket socket;

    private ChannelHandler handler;

    private Charset charset;

    public ChannelAdapter(Socket socket, Charset charset) {
        this.socket = socket;
        this.charset = charset;
        while (!socket.isConnected()) {
            break;
        }

        handler = new ChannelHandler(this.socket, this.charset);
        channelActive(handler);
    }

    @Override
    public void run() {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            String str = null;
            while ((str = input.readLine()) != null) {
                channelRead(handler, str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 链接通知抽象类
    public abstract void channelActive(ChannelHandler ctx);

    // 读取消息抽象类
    public abstract void channelRead(ChannelHandler ctx, Object msg);
}
