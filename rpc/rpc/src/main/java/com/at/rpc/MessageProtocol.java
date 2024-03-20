package com.at.rpc;

import io.netty.buffer.ByteBuf;

import java.util.Arrays;

public class MessageProtocol {

    // 系统编号 0xA
    private byte type;

    // 信息标识：心跳 0xA，业务 0xB，超时 0xC
    private byte flag;

    private int length;

    private byte[] content;

    private long requestId;

    private ByteBuf inputBuf;

    public MessageProtocol(byte type, byte flag, int length, byte[] content, long requestId) {
        this.type = type;
        this.flag = flag;
        this.length = length;
        this.content = content;
        this.requestId = requestId;
    }

    public void release() {
        inputBuf.release();
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getFlag() {
        return flag;
    }

    public void setFlag(byte flag) {
        this.flag = flag;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public ByteBuf getInputBuf() {
        return inputBuf;
    }

    public void setInputBuf(ByteBuf inputBuf) {
        this.inputBuf = inputBuf;
    }

    @Override
    public String toString() {
        return "MessageProtocol{" +
                " type=" + type +
                ", flag=" + flag +
                ", length=" + length +
                ", content=" + Arrays.toString(content) +
                ", requestId=" + requestId +
                '}';
    }
}
