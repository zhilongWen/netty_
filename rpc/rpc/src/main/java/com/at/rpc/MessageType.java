package com.at.rpc;

public enum MessageType {

    SERVER((byte) 0),
    CLIENT((byte) 1);

    public final byte value;

    private MessageType(byte value) {
        this.value = value;
    }


}
