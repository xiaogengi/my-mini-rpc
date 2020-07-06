package com.xg.nio.provider;

import com.xg.nio.api.IRpcService;
import com.xg.nio.monitor.annotation.Monitor;

/**
 * @program: nio-rpc
 * @description:
 * @author: gzk
 * @create: 2020-06-01 14:37
 **/
@Monitor
public class RpcServiceImpl implements IRpcService {
    @Override
    public int add(int a, int b) {
        return a + b;
    }

    @Override
    public int sub(int a, int b) {
        return a - b;
    }

    @Override
    public int mult(int a, int b) {
        return a * b;
    }

    @Override
    public int div(int a, int b) {
        return a / b;
    }
}
