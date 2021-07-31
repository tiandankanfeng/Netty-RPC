package com.cloud.rpc.common.provider.constants;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;


@Slf4j
public class NettyServerConstants {

    public static final String inetHost;
    public static final Integer port;

    static {

        inetHost = "192.168.0.198";
        port = 9898;
    }

}
