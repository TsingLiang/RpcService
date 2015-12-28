package com.tsing;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Random;

public class RpcProxy 
{
	private ServiceDiscovery serviceDiscovery;
	
	public RpcProxy(ServiceDiscovery serviceDiscovery)
	{
		this.serviceDiscovery = serviceDiscovery;
	}
	
	public <T> T create(Class<?> interfaceClass)
	{
		return (T) Proxy.newProxyInstance(
							interfaceClass.getClassLoader(),
							new Class<?>[]{interfaceClass},
							new InvocationHandler()
							{
								public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
								{	
									RpcRequest request = new RpcRequest();
							        request.setClassName(method.getDeclaringClass().getName());
							        request.setMethodName(method.getName());
							        request.setParameterTypes(method.getParameterTypes());
							        request.setParameters(args);
							        
							        System.out.println(request);
							        
							        String serverAddress = serviceDiscovery.getNext();
							        String[] ipPort = serverAddress.split(":");
							        String ip = ipPort[0];
							        int port = Integer.parseInt(ipPort[1]);;
							        
							        System.out.println("ip: " + ip);
							        System.out.println("port: " + port);
							        
							        RpcClient client = new RpcClient(ip, port);
				                    RpcResponse response = client.send(request);

				                    System.out.println(request);
				                    
				                    if (response.getError() != null) 
				                    {
				                        throw response.getError();
				                    } 
				                    else 
				                    {
				                        return response.getResult();
				                    }
								}
							});
	}
}
