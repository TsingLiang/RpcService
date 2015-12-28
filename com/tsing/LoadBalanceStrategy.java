package com.tsing;

import java.util.List;

public interface LoadBalanceStrategy 
{
	public String getNext(List<String> servers);
}
