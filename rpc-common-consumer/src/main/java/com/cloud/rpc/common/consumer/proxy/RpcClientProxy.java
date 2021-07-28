package com.cloud.rpc.common.consumer.proxy;


import com.alibaba.fastjson.JSON;
import com.cloud.rpc.common.RpcRequest;
import com.cloud.rpc.common.RpcResponse;
import com.cloud.rpc.common.consumer.client.NettyRpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * proxy for Rpc-Client
 */
@Component
public class RpcClientProxy {

    @Autowired
    private NettyRpcClient rpcClient;

    static final Map<Class, Object> SERVICE_PROXY_MAP = new ConcurrentHashMap<>(16);

    public Object getProxy(Class clazz) {

        var proxy = SERVICE_PROXY_MAP.get(clazz);
        if (null != proxy) {
            return proxy;
        }
        // new a proxy
        proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{clazz},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // 封装请求对象
                        RpcRequest rpcRequest = new RpcRequest();
                        rpcRequest.setRequestId(UUID.randomUUID().toString());
                        rpcRequest.setMethodName(method.getName());
                        rpcRequest.setClassName(method.getDeclaringClass().getName());
                        rpcRequest.setParameters(args);
                        rpcRequest.setParameterType(method.getParameterTypes());

                        try {
                            //  send msg and decorate response
                            Object msg = rpcClient.sndMsg(JSON.toJSONString(rpcRequest));
                            RpcResponse response = JSON.parseObject(msg.toString(), RpcResponse.class);

                            if (null != response.getError()) {
                                throw new RuntimeException(response.getError());
                            }

                            var res = response.getResult();
                            if (null != res) {
                                return JSON.parseObject(res.toString(), method.getReturnType());
                            }

                            return null;
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw e;
                        }


                    }
                });
        SERVICE_PROXY_MAP.put(clazz, proxy);
        return proxy;
    }
}
