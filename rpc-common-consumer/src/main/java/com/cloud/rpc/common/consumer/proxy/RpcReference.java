package com.cloud.rpc.common.consumer.proxy;


import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 引用代理类
 * 即，实现将代理注入到接口中去
 */
@Target(ElementType.FIELD) // 作用于字段
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcReference {
}
