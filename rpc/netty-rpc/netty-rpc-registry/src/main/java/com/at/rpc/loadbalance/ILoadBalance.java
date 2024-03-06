package com.at.rpc.loadbalance;

import java.util.List;

public interface ILoadBalance<T> {
    T select(List<T> servers);
}
