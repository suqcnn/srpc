package com.srpc.server;

import com.srpc.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by zynb0319 on 2016/1/23.
 */
public class RpcServer implements ApplicationContextAware,InitializingBean{
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);
    private String serverAddress;
    private ServiceRegistry serviceRegistry;
    public RpcServer(String serverAddress) {
        this.serverAddress = serverAddress;
    }
    public RpcServer(String serverAddress, ServiceRegistry serviceRegistry) {
        this.serverAddress = serverAddress;
        this.serviceRegistry = serviceRegistry;
    }
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        Map<String,Object> serviceBeanMap = ctx.getBeanFro
    }

    public void afterPropertiesSet() throws Exception {

    }
}
