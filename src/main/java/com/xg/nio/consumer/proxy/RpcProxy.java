package com.xg.nio.consumer.proxy;

import com.xg.nio.protocol.InvokerProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @program: nio-rpc
 * @description:
 * @author: gzk
 * @create: 2020-06-01 15:23
 **/
public class RpcProxy {

    /**
     * 把 class 在 MethodProxy 中赋值到 clazz 成员变量中
     * 判断该class 是否为接口， 是接口 通过 getInterfaces() 获取 。如果不是 new Class[]{}
     * 通过动态代理创建该 class 的代理对象 并返回
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T create(Class<?> clazz){
        MethodProxy proxy = new MethodProxy(clazz);
        Class<?> [] interfaces = clazz.isInterface() ? new Class[]{clazz} : clazz.getInterfaces();
        T result = (T) Proxy.newProxyInstance(clazz.getClassLoader(), interfaces, proxy);
        return result;
    }

    private static class MethodProxy implements InvocationHandler {
        private Class<?> clazz;

        public MethodProxy(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if(Object.class.equals(method.getDeclaringClass())){
                try {
                    return method.invoke(this, args);
                } catch (Throwable t){
                    t.printStackTrace();
                }
            }else{
                return rpcInvoke(proxy, method, args);
            }
            return null;
        }

        private Object rpcInvoke(Object proxy, Method method, Object[] args) {

            // 传输协议封装
            InvokerProtocol msg = new InvokerProtocol();
            msg.setClassName(this.clazz.getName());
            msg.setMethodName(method.getName());
            msg.setParams(method.getParameterTypes());
            msg.setValues(args);

            final RpcProxyHandler consumerHandler = new RpcProxyHandler();
            EventLoopGroup group = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(group)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .handler(new ChannelInitializer<SocketChannel>() {

                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                ChannelPipeline pipeline = socketChannel.pipeline();
                                /**
                                 * 自定义协议解码器
                                 * 入参有5个
                                 * maxFrameLength: 框架的最大长度，如果帧的长度大于此值，则抛出 TooLongFrameException
                                 * lengthFieldOffset: 长度字段的偏移量， 即对应的长度字段在整个消息数据中的位置
                                 * lengthFieldLength: 长度字段的长度， 如：长度字段是 int 表示，这个值就是4 （long 8）
                                 * lengthAdjustment: 要添加到长度字段值的补偿值
                                 * initialBytesToStrip: 从解码帧中去除第一个字节数
                                 */
                                // 自定义协议 编码器
                                pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0,4,0,4));
                                pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));

                                // 对象参数类型编码器
                                pipeline.addLast("encoder", new ObjectEncoder());
                                pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));

                                pipeline.addLast("handler", consumerHandler);
                            }
                        });

                ChannelFuture future = bootstrap.connect("localhost", 8080).sync();
                future.channel().writeAndFlush(msg).sync();
                future.channel().closeFuture().sync();
            } catch (Exception e){
                e.printStackTrace();
            }finally {
                group.shutdownGracefully();
            }

            return consumerHandler.getResponse();
        }
    }
}
