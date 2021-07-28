package com.cloud.rpc.common.consumer.controller;



import com.cloud.rpc.api.UserService;
import com.cloud.rpc.common.consumer.proxy.RpcReference;
import com.cloud.rpc.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user")
public class UserController {

    @RpcReference
    private UserService userService;

    @GetMapping("/{id}")
    public User getById(@PathVariable("id") Integer id) {
        return userService.getById(id);
    }
}
