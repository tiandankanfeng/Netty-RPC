package com.cloud.rpc.common;


import lombok.Data;

/**
 * 封装响应对象
 */
@Data
public class RpcResponse {

    /**
     * 响应 id
     */
    private String responseId;
    /**
     * 错误信息
     */
    private String error;
    /**
     * 返回的结果
     */
    private Object result;
}
