package com.cloud.rpc.common.provider.server;

import com.cloud.rpc.common.provider.constants.NettyServerConstants;
import com.cloud.rpc.common.provider.handler.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Netty服务端
 */
@Component // 能够被 Spring管理
public class NettyRpcServer implements DisposableBean {

    @Autowired
    private NettyServerHandler nettyServerHandler;

    NioEventLoopGroup bossGroup = null;
    NioEventLoopGroup workerGroup = null;

    public void start(String inetHost, Integer inetPort) {

        if (StringUtils.isEmpty(inetHost) || StringUtils.isEmpty(inetPort)) {
            startUp(NettyServerConstants.inetHost, NettyServerConstants.port);
        } else {
            startUp(inetHost, inetPort);
        }
    }

    public void startUp(String inetHost, Integer inetPort) {



        try {
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();

            ChannelFuture future = new ServerBootstrap()
                .group(workerGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline().addLast(new StringDecoder());
                        channel.pipeline().addLast(new StringEncoder());
                        // add self handler
                        channel.pipeline().addLast(nettyServerHandler);
                    }
                })
                .bind(inetHost, inetPort).sync();

            System.out.println("Netty server start successfully!");

            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            shutdownGracefully();
        }
    }

    public void shutdownGracefully() {
        if (null != bossGroup) bossGroup.shutdownGracefully();
        if (null != workerGroup) workerGroup.shutdownGracefully();
    }

    /**
     * 容器关闭时, Netty服务端也需要被关闭
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        shutdownGracefully();
    }
}
