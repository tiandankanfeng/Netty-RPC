package com.cloud.rpc.common.provider.service;

import com.cloud.rpc.api.UserService;
import com.cloud.rpc.common.provider.server.RpcService;
import com.cloud.rpc.pojo.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

//@Service
@RpcService(clazz = UserService.class) // 指定远程接口
public class UserServiceImpl implements UserService {

    private volatile Map<Object, User> map;

    @Override
    public User getById(Integer id) {
        if (null == map) {
            synchronized (UserServiceImpl.class) {
                if (null == map) {
                    map = new HashMap<>();
                }
            }
        }

        if (map.size() == 0) {
            map.put(1, new User().setId(1).setName("liangye"));
            map.put(2, new User().setId(2).setName("lake"));
        }
        return map.get(id);
    }
}
