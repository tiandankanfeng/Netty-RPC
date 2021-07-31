package com.cloud.rpc.common.provider;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootTest
class RpcCommonServerApplicationTests {

    @Test
    void contextLoads() throws UnknownHostException {
        System.out.println(InetAddress.getLocalHost().getHostAddress());
    }

}
