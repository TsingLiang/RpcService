package com.tsing;

import java.util.List;
import java.util.Random;

public class RandomRobin implements LoadBalanceStrategy 
{
	private Random random = new Random();
	
	public String getNext(List<String> servers) 
	{
		synchronized(this)
		{
			return servers.get(random.nextInt(servers.size()));
		}
	}
}
