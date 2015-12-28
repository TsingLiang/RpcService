package com.tsing;

@RpcService(Hello.class)
public class HelloImp implements Hello 
{
	public String hello(String name)
	{
		return "hello " + name + ".";
	}
}
