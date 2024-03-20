package com.at.rpc;

public enum MessageFlag {

    UNKOWN((byte) 0),
    HEARBEAT((byte) 1),
    TIMEOUT((byte) 2),
    DATA((byte) 3);

    public final byte value;

    private MessageFlag(byte value) {
        this.value = value;
    }
}
