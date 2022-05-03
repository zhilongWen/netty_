package com.at.rpc.provider;

import com.at.rpc.publicinter.Service;

/**
 * @create 2022-05-03
 */
public class ServiceImpl implements Service {

    @Override
    public String hello(String info) {

        System.out.println("接收 client 端信息 = " + info);

        return "查询数据库~~~~";
    }


}
