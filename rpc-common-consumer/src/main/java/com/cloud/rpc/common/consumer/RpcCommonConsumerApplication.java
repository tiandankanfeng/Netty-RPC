package com.cloud.rpc.common.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class RpcCommonConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpcCommonConsumerApplication.class, args);
    }

}
