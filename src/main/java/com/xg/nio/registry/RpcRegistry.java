package com.xg.nio.registry;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @program: nio-rpc
 * @description:
 * @author: gzk
 * @create: 2020-06-01 14:38
 **/
public class RpcRegistry {

    private int port;
    public RpcRegistry(int port){
        this.port = port;
    }

    public void start(){

        EventLoopGroup loopGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(loopGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0 , 4 , 0, 4));
                            pipeline.addLast(new LengthFieldPrepender(4));
                            pipeline.addLast("encoder",new ObjectEncoder());
                            pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                            pipeline.addLast(new RegisterHandler());
                        }
                    })
            .option(ChannelOption.SO_BACKLOG, 128)
            .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = b.bind(port).sync();
            System.out.println("Xg RPC Registry start listen at " + port);
            future.channel().closeFuture().sync();
        } catch (Exception e){
            loopGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new RpcRegistry(8080).start();
    }

}
