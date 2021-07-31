package com.cloud.rpc.common.consumer.controller;



import com.cloud.rpc.api.UserService;
import com.cloud.rpc.common.consumer.proxy.RpcReference;
import com.cloud.rpc.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user")
public class UserController {

    @RpcReference
    private UserService userService;
    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @GetMapping("/{id}")
    public User getById(@PathVariable("id") Integer id) {
        return userService.getById(id);
    }

    @GetMapping("/getServiceInfoByName/{serviceName}")
    public String getServiceInfoByName(@PathVariable("serviceName") String serviceName) {
        ServiceInstance instance = loadBalancerClient.choose(serviceName);
        return instance.toString();
    }



}
