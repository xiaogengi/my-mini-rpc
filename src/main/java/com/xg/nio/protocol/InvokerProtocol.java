package com.xg.nio.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: nio-rpc
 * @description: 自定义协议类
 * @author: gzk
 * @create: 2020-06-01 14:34
 **/
@Data
public class InvokerProtocol implements Serializable {

    private String className; // 类名
    private String methodName;// 方法名
    private Class<?> [] params;  // 参数类型
    private Object[] values;  // 参数列表

}
