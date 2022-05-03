package com.at.rpc_.customer;

import com.at.rpc_.netty.NettyClient;
import com.at.rpc_.publicinter.Service;

/**
 * @create 2022-05-03
 */
public class ClientStart {

    public static void main(String[] args) {

        NettyClient customer = new NettyClient();


        Service service = (Service) customer.getBean(Service.class);

//       for (;;){
           String result = service.hello("hello，netty");
           System.out.println("result=" + result);

//       }

    }
}
