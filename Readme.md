*目前存在的缺点：对端写死，应写入注册中心等组件实现服务的注册与发现，对服务进行更好的管理*
*Netty server监听端口可以约定好，但地址是不确定的，so，需要借助于注册中心*
*目前已整合 Consul实现注册中心功能*