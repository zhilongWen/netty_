package com.at.rpc_.provider;

import com.at.rpc_.netty.NettyService;

/**
 * @create 2022-05-03
 */
public class ServerStart {

    public static void main(String[] args) {

        NettyService.start("127.0.0.1",8090);

    }

}
