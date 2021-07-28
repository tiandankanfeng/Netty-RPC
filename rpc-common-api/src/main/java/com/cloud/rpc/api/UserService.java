package com.cloud.rpc.api;

import com.cloud.rpc.pojo.User;

/**
 * 公共接口
 */
public interface UserService {

    /**
     * 根据 id查询用户
     * @param id
     * @return
     */
    User getById(Integer id);
}
