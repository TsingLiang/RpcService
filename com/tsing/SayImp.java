package com.tsing;

@RpcService(Say.class)
public class SayImp implements Say 
{
	public String say(String name)
	{
		return "say " + name + ".";
	}
}
