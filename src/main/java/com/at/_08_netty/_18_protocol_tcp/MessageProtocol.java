package com.at._08_netty._18_protocol_tcp;

/**
 * @create 2022-04-30
 */
public class MessageProtocol {

    private int len;
    private byte[] content;


    public MessageProtocol() {
    }

    public MessageProtocol(int len, byte[] content) {
        this.len = len;
        this.content = content;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
