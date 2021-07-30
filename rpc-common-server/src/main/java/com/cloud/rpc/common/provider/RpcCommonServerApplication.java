package com.cloud.rpc.common.provider;

import com.alibaba.nacos.spring.context.annotation.discovery.EnableNacosDiscovery;
import com.cloud.rpc.common.provider.server.NettyRpcServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * CommandLineRunner - 用于应用程序初始化后, 执行一段有逻辑的代码, 而这块初始化代码在整个应用程序生命周期中也只会被执行一次
 */
//@EnableDiscoveryClient
@SpringBootApplication
//@EnableNacosDiscovery
public class RpcCommonServerApplication implements CommandLineRunner {

    @Autowired
    private NettyRpcServer rpcServer;

    public static void main(String[] args) {
        SpringApplication.run(RpcCommonServerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        new Thread(() -> {
            rpcServer.start(null, null);
        }).start();
    }
}
