package com.at.rpc.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class RequestHolder {
    public static final AtomicLong REQUEST_ID=new AtomicLong();

    public static final Map<Long,RpcFuture> REQUEST_MAP=new ConcurrentHashMap<>();
}
