package com.xg.nio.provider;

import com.xg.nio.api.IHelloService;
import com.xg.nio.monitor.annotation.Monitor;

/**
 * @program: nio-rpc
 * @description:
 * @author: gzk
 * @create: 2020-06-01 14:37
 **/
@Monitor
public class RpcHelloServiceImpl implements IHelloService {
    @Override
    public String hello(String name) {
        return "hi ~ " + name;
    }
}
