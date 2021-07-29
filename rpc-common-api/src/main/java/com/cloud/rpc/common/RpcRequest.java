package com.cloud.rpc.common;


import lombok.Data;

/**
 * 封装请求对象
 */
@Data
public class RpcRequest {
    /**
     * 请求对象id
     * 实现关联
     */
    private String requestId;
    private String className;
    private String methodName;
    private Class<?>[] parameterType;
    /**
     * 入参
     */
    private Object[] parameters;
}
