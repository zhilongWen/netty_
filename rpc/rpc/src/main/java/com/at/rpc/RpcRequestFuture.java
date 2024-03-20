package com.at.rpc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class RpcRequestFuture {
    public static final Map<Long, RpcFuture> SYNC_WRITE_MAP = new ConcurrentHashMap<>();
}
