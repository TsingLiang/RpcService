package com.tsing;

import java.lang.reflect.Method;
import java.util.Map;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RpcHandler extends SimpleChannelInboundHandler<RpcRequest> 
{
    private final Map<String, Object> handlerMap;

    public RpcHandler(Map<String, Object> handlerMap) 
    {
        this.handlerMap = handlerMap;
    }
    
    @Override
    public void channelRead0(final ChannelHandlerContext ctx, RpcRequest request) throws Exception 
    {
    	System.out.println("server receive: " + request);
        RpcResponse response = new RpcResponse();
        try 
        {
            Object result = handle(request);
            response.setResult(result);
        } 
        catch (Throwable t) 
        {
            response.setError(t);
        }
        
        System.out.println("server send: " + response);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private Object handle(RpcRequest request) throws Throwable 
    {
        String className = request.getClassName();
        Object serviceBean = handlerMap.get(className);

        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        Method method = serviceClass.getMethod(methodName, parameterTypes);
        method.setAccessible(true);
        
        return method.invoke(serviceBean, parameters);
    }
}
