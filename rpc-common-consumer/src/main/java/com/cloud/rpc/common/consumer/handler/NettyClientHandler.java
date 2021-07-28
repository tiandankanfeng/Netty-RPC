package com.cloud.rpc.common.consumer.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

/**
 * 客户端业务处理器
 */
@Component
//@ChannelHandler.Sharable
@Data
public class NettyClientHandler extends SimpleChannelInboundHandler<String> implements Callable {

    private ChannelHandlerContext ctx;
    // 请求消息
    private String reqMsg;
    // 响应消息
    private String respMsg;

    @Override
    protected synchronized void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        respMsg = s;
        notify();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        super.channelActive(ctx);
    }

    @Override
    public synchronized Object call() throws Exception {
        ctx.writeAndFlush(reqMsg);
        /**
         * 借助 thread wait/notify attain 伪同步
         */
        wait();
        return respMsg;
    }
}
