package com.tsing;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobin implements LoadBalanceStrategy
{	
	private AtomicInteger next = new AtomicInteger(0);
	
	public String getNext(List<String> servers) 
	{
		return servers.get(next.addAndGet(1) % servers.size());
	}
}
