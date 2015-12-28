package com.tsing;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class RpcServer implements ApplicationContextAware, InitializingBean 
{
    private String serverAddress;
    private ServiceRegistry serviceRegistry;

    private Map<String, Object> handlerMap = new HashMap<String, Object>();
    
    public RpcServer(String serverAddress, ServiceRegistry serviceRegistry)
    {
        this.serverAddress = serverAddress;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException
    {
        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);
        if (MapUtils.isNotEmpty(serviceBeanMap)) 
        {
            for (Object serviceBean : serviceBeanMap.values()) 
            {
                String interfaceName = serviceBean.getClass().getAnnotation(RpcService.class).value().getName();
                handlerMap.put(interfaceName, serviceBean);
            }
        }
        
        System.out.println("get services: " + handlerMap);
    }

    @Override
    public void afterPropertiesSet()
    {
    	System.out.println("afterPropertiesSet.");
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try 
        {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() 
                {
                    public void initChannel(SocketChannel channel) throws Exception 
                    {
                        channel.pipeline()
                            .addLast(new RpcDecoder(RpcRequest.class)) 
                            .addLast(new RpcEncoder(RpcResponse.class))
                            .addLast(new RpcHandler(handlerMap));
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

            String[] array = serverAddress.split(":");
            String host = array[0];
            int port = Integer.parseInt(array[1]);
            
            System.out.println("host: " + host);
            System.out.println("port: " + port);
            
            ChannelFuture future = bootstrap.bind(host, port).sync();

            for (String serviceBean : handlerMap.keySet()) 
            {
            	serviceRegistry.registry(serviceBean, serverAddress);
                System.out.println("registry: " + serviceBean);
            }
            
            future.channel().closeFuture().sync();
        } 
        catch (Exception e)
        {
        	e.printStackTrace();
        }
        finally 
        {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }  
}
