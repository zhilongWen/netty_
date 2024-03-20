package com.at.rpc.bio;


import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

public class ChannelHandler {

    private Socket socket;

    private Charset charset;

    public ChannelHandler(Socket socket, Charset charSet){
        this.socket = socket;
        this.charset = charSet;
    }

    public void writeAndFlush(Object msg) {

        OutputStream out = null;

        try {
            out = socket.getOutputStream();
            out.write(msg.toString().getBytes(charset));
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public Socket socket(){
        return socket;
    }


}
