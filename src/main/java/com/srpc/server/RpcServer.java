package com.srpc.server;

import com.srpc.annotation.RpcService;
import com.srpc.handler.RpcDecoder;
import com.srpc.handler.RpcEncoder;
import com.srpc.handler.RpcHandler;
import com.srpc.model.RpcRequest;
import com.srpc.model.RpcResponse;
import com.srpc.registry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zynb0319 on 2016/1/23.
 */
public class RpcServer implements ApplicationContextAware,InitializingBean{
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);
    private String serverAddress;
    private ServiceRegistry serviceRegistry;
    private Map<String, Object> handlerMap = new HashMap<String,Object>(); // 存放接口名与服务对象之间的映射关系
    public RpcServer(String serverAddress) {
        this.serverAddress = serverAddress;
    }
    public RpcServer(String serverAddress, ServiceRegistry serviceRegistry) {
        this.serverAddress = serverAddress;
        this.serviceRegistry = serviceRegistry;
    }
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        Map<String,Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);
        if(MapUtils.isEmpty(serviceBeanMap)){
            for(Object serviceBean:serviceBeanMap.values()){
                String interfaceName = serviceBean.getClass().getAnnotation(RpcService.class).value().getName();
                handlerMap.put(interfaceName,serviceBean);
            }
        }
    }

    public void afterPropertiesSet() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new RpcDecoder(RpcRequest.class))
                                    .addLast(new RpcEncoder(RpcResponse.class))
                                    .addLast(new RpcHandler(handlerMap));
                        }
                    }).option(ChannelOption.SO_BACKLOG,128)
                    .option(ChannelOption.SO_KEEPALIVE,true);
            String[] array = serverAddress.split(":");
            String host = array[0];
            int port = Integer.parseInt(array[1]);
            ChannelFuture channelFuture = b.bind(host,port).sync();
            LOGGER.debug("server started at port:"+port+",host:"+host);
            if(serviceRegistry!=null){
                serviceRegistry.register(serverAddress);
            }
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
