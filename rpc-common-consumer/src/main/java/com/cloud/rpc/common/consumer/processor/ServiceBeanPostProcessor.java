package com.cloud.rpc.common.consumer.processor;

import com.cloud.rpc.common.consumer.proxy.RpcClientProxy;
import com.cloud.rpc.common.consumer.proxy.RpcReference;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class ServiceBeanPostProcessor implements BeanPostProcessor {

    @Autowired
    private RpcClientProxy clientProxy;

    /**
     * 实现自定义注解的注入
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException  {
        // get all fields of bean
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            // 查询是否有需要去代理的接口
            if (field.isAnnotationPresent(RpcReference.class)) {
                Object proxy = clientProxy.getProxy(field.getType());
                try {
                    field.setAccessible(true);
                    field.set(bean, proxy);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
//            RpcReference annotation = field.getAnnotation(RpcReference.class);
//            if (null != annotation) {
//                Object proxy = clientProxy.getProxy(field.getType());
//                try {
//                    field.setAccessible(true);
//                    field.set(bean, proxy);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
        }
        return bean;
    }
}
