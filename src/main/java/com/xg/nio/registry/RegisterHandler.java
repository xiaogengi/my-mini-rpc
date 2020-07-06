package com.xg.nio.registry;

import com.xg.nio.protocol.InvokerProtocol;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInboundInvoker;
import io.netty.util.concurrent.EventExecutorGroup;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: nio-rpc
 * @description:
 * @author: gzk
 * @create: 2020-06-01 14:59
 **/
public class RegisterHandler extends ChannelInboundHandlerAdapter {

    public static List<String> classNames = new ArrayList();
    public static Map<String,Object> registryMap = new ConcurrentHashMap<>();

    public RegisterHandler() {
        scannerClass("com.xg.nio.provider");
        doRegistry();
        System.out.println("");
    }

    private void doRegistry() {
        if(classNames.size() == 0){
            return;
        }
        for (String className : classNames) {

            try {
                Class<?> clazz = Class.forName(className);
                Class<?> i = clazz.getInterfaces()[0];
                registryMap.put(i.getName(), clazz.newInstance());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void scannerClass(String packageName) {
        URL url = this.getClass().getClassLoader().getResource(packageName.replaceAll("\\.", "/"));
        File file = new File(url.getFile());
        for (File listFile : file.listFiles()) {
            // 如果是文件夹 继续查询
            if(listFile.isDirectory()){
                scannerClass(packageName + "." + listFile.getName());
            }else{
                classNames.add(packageName + "." + listFile.getName().replaceAll(".class","").trim());
            }
        }

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Object result = new Object();
        InvokerProtocol request = (InvokerProtocol) msg;
        if(registryMap.containsKey(request.getClassName())){
            Object clazz = registryMap.get(request.getClassName());
            Method method = clazz.getClass().getMethod(request.getMethodName(), request.getParams());
            result = method.invoke(clazz, request.getValues());
        }
        ctx.write(result);
        ctx.flush();
        ctx.close();

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();

    }
}
