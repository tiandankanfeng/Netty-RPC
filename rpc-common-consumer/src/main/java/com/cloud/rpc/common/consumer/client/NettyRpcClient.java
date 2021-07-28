package com.cloud.rpc.common.consumer.client;

import com.cloud.rpc.common.consumer.handler.NettyClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * Netty客户端
 * InitializingBean - 当我们 Client创建完之后, 便可以去连接 Netty Server了
 */
@Component
public class NettyRpcClient implements InitializingBean, DisposableBean {

    @Autowired
    private NettyClientHandler clientHandler;

    private NioEventLoopGroup group = new NioEventLoopGroup();
    private Channel ch;
    private ExecutorService executor = Executors.newCachedThreadPool(); // 仅个人开发时这么写


    @Override
    public void afterPropertiesSet() throws Exception {

        try {
            ChannelFuture future = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel channel) throws Exception {
                            channel.pipeline().addLast(new StringDecoder());
                            channel.pipeline().addLast(new StringEncoder());
                            // business process
                            channel.pipeline().addLast(clientHandler);
                        }
                    }).connect("127.0.0.1", 9898).sync();

            System.out.println("client connect sucessfully!");

            ch = future.channel();

        } catch (Exception e) {
            e.printStackTrace();
            shutdownGracefully();
        }


    }

    public Object sndMsg(String msg) throws ExecutionException, InterruptedException {
        clientHandler.setReqMsg(msg);
        Future future = executor.submit(clientHandler);
        return future.get();
    }

    @Override
    public void destroy() throws Exception {
        shutdownGracefully();
    }

    public void shutdownGracefully() {
        if (null != group) {
            group.shutdownGracefully();
        }
        if (null != ch) {
            ch.close();
        }
    }
}
