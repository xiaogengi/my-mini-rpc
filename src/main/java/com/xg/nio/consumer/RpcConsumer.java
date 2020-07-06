package com.xg.nio.consumer;

import com.xg.nio.api.IHelloService;
import com.xg.nio.api.IRpcService;
import com.xg.nio.consumer.proxy.RpcProxy;
import com.xg.nio.provider.RpcServiceImpl;

/**
 * @program: nio-rpc
 * @description:
 * @author: gzk
 * @create: 2020-07-06 10:49
 **/
public class RpcConsumer {

    public static void main(String[] args) {
        IHelloService rpcHello = RpcProxy.create(IHelloService.class);
        System.out.println(rpcHello.hello("tom"));

        IRpcService rpcService = RpcProxy.create(IRpcService.class);

        System.out.println("1 + 2 = " + rpcService.add(1,2));
        System.out.println("1 - 2 = " + rpcService.sub(1,2));
        System.out.println("1 * 2 = " + rpcService.mult(1,2));
        System.out.println("1 / 2 = " + rpcService.div(1,2));
    }

}
