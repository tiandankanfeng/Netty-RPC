package com.cloud.rpc.common.provider.handler;

import com.alibaba.fastjson.JSON;
import com.cloud.rpc.common.RpcRequest;
import com.cloud.rpc.common.RpcResponse;
import com.cloud.rpc.common.provider.server.RpcService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * 自定义业务处理类
 * 1.缓存带有 @RpcService的 bean
 * 2.接受客户端请求
 * 3.根据传递过来的 beanName, 去缓存中查找对应的 bean
 * 4.基于反射调用其对应的方法
 * 5.响应客户端
 *
 * ApplicationContextAware - 从 Spring容器中获取已实例化的 bean
 */
@Slf4j
@Component
@ChannelHandler.Sharable // 单例默认不可共享, 这里来设置下可以共享
public class NettyServerHandler extends SimpleChannelInboundHandler<String> implements ApplicationContextAware {

    static final Map<String, Object> SERVICE_INSTANCE_MAP = new ConcurrentHashMap<>();

    /**
     * 事件就绪, 异步回调
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        // 解析客户端请求
        RpcRequest rpcRequest = JSON.parseObject(s, RpcRequest.class);
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setResponseId(rpcRequest.getRequestId());

        // business process
        try {
            rpcResponse.setResult(handler(rpcRequest));
        } catch (Exception e) {
            rpcResponse.setError(e.getMessage());
            e.printStackTrace();
        }

        // 响应客户端
        ctx.writeAndFlush(JSON.toJSONString(rpcResponse));
    }

    /**
     * Reflective method invoke
     * @param rpcRequest
     * @return
     */
    private Object handler(RpcRequest rpcRequest) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        var className = rpcRequest.getClassName();
        var methodName = rpcRequest.getMethodName();

        Assert.notNull(className);
        Assert.notNull(methodName);


        Object bean = SERVICE_INSTANCE_MAP.get(className);
        if (null == bean) {
            log.warn("服务端未找到对应服务, rpcRequest:{}", rpcRequest);
            throw new RuntimeException("服务端未找到对应服务");
        }
        // Reflective invoke
//        FastClass | JdkDynamicProxy
        Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), bean.getClass().getInterfaces(),
                 new InvocationHandler() {
                     @Override
                     public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                         return method.invoke(bean, args);
                     }
                }
        );

        Class<?> intr = bean.getClass().getInterfaces()[0];
//        proxy = intr.cast(proxy);

        Method method = proxy.getClass().getDeclaredMethod(rpcRequest.getMethodName(), rpcRequest.getParameterType());
        return method.invoke(proxy, rpcRequest.getParameters());


//        FastClass proxy = FastClass.create(bean.getClass());
//        FastMethod method = proxy.getMethod(methodName, rpcRequest.getParameterType());

//        return method.invoke(bean, rpcRequest.getParameters());

    }

    // cache @RpcService bean
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(RpcService.class);
        Set<Map.Entry<String, Object>> entries = beans.entrySet();

        for (Map.Entry<String, Object> entry : entries) {
            Object bean = entry.getValue();

            if (bean.getClass().getInterfaces().length == 0) {
                throw new RuntimeException("对外暴露的服务必须要去实现接口");
            }
            // 默认去缓存第一个实现的接口
            String serviceName = bean.getClass().getInterfaces()[0].getName();
            SERVICE_INSTANCE_MAP.put(serviceName, bean);
        }
    }
}
