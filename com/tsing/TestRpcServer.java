package com.tsing;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestRpcServer 
{
	public static void main(String[] args)
	{
		ApplicationContext ctx = new ClassPathXmlApplicationContext("/com/tsing/server.xml");
		//RpcServer rpcServer = (RpcServer) ctx.getBean("rpcServer");
		//RpcServer rpcServer = new RpcServer("127.0.0.1:5000", serviceRegistry);
		//RpcServer rpcServer = (RpcServer) ctx.getBean("rpcServer");
		System.out.println("over.");
	}
}
