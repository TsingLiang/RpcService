package com.tsing;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestRpcClient 
{
	public static void main(String[] args)
	{
		ApplicationContext ctx = new ClassPathXmlApplicationContext("/com/tsing/client.xml");
		RpcProxy rpcProxy = (RpcProxy) ctx.getBean("rpcProxy");
		Hello hello = rpcProxy.create(Hello.class);
		System.out.println(hello.hello("Alice"));
		System.out.println("over.");
	}
}
