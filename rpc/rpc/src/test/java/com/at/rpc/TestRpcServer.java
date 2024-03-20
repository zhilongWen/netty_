package com.at.rpc;

import com.at.rpc.server.RpcServer;
import com.at.rpc.server.RpcServerProcess;
import io.netty.channel.ChannelHandlerContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class TestRpcServer {
    public static void main(String[] args) {

        RpcServerProcess process = new TestRpcServerProcess();

        RpcServer rpcServer = new RpcServer("192.168.43.159",9909,process);
        rpcServer.start();

    }
}

class TestRpcServerProcess extends RpcServerProcess{
    @Override
    public void process(ChannelHandlerContext ctx, MessageProtocol msg) {
        System.out.println("msg = " + msg);
//        send(ctx,"server msg");

        try {

            ByteArrayInputStream bis = new ByteArrayInputStream(msg.getContent());
            ObjectInputStream in = new ObjectInputStream(bis);

            String result = in.readObject().toString();

            in.close();

            System.out.println("result = " + result);


            if (result.contains("1")){
                System.out.println("sleep");
                Thread.sleep(10000);
                System.out.println("sleep done");
            }

            send(ctx,msg.getRequestId(),result);


        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }
}
