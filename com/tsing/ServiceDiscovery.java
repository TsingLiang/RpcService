package com.tsing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event;

public class ServiceDiscovery
{
	private static final int SESSION_TIMEOUT = 5000;
	private static final String REGISTRY_PATH = "/registry";
	
	private CountDownLatch latch; 
	private String registryAddress;
	private String service;
	
	private volatile List<String> servers = new ArrayList<String>();
	private LoadBalanceStrategy strategy;
	
	public ServiceDiscovery(String registryAddress, String service, LoadBalanceStrategy strategy)
	{
		this.registryAddress = registryAddress;
		this.service = service;
		this.strategy = strategy;
		
		ZooKeeper zk = connectServer();
		System.out.println(zk);
		if(zk != null)
		{
			discover(zk);
		}
	}
	
	public String getNext()
	{
		return strategy.getNext(servers);
	}
	
	private void discover(final ZooKeeper zk)
	{
		try 
		{
			servers = zk.getChildren(REGISTRY_PATH + "/" + service, new Watcher()
			{
				public void process(WatchedEvent event)
				{
					if(event.getType() == Event.EventType.NodeChildrenChanged)
					{
						discover(zk);
					}
				}
			});
			
			System.out.println(servers);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	/*
	private void watchNode(final ZooKeeper zk, final String node, final Map<String, List<String>> services)
	{
		try
		{
			List<String> nodes = zk.getChildren(REGISTRY_PATH + "/" + node, new Watcher()
			{
				public void process(WatchedEvent event)
				{
					if(event.getType() == Event.EventType.NodeChildrenChanged)
					{
						watchNode(zk, node, services);
					}
				}
			});
			
			services.remove(node);
			services.put(node, nodes);
			
			System.out.println(services);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	*/
	private ZooKeeper connectServer()
	{
		ZooKeeper zk = null;
		latch = new CountDownLatch(1);
		try
		{
			zk = new ZooKeeper(registryAddress, SESSION_TIMEOUT, new Watcher(){
				public void process(WatchedEvent event)
				{
					if(event.getState() == Event.KeeperState.SyncConnected)
					{
						latch.countDown();
					}
				}
			});

			latch.await();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return zk;
	}
	
	public static void main(String[] args) throws Exception
	{
		ServiceDiscovery discovery = new ServiceDiscovery("127.0.0.1:2181", "Hello", new RandomRobin());
		while(true)
		{
			Thread.sleep(1000);
			System.out.println(discovery.getNext());
		}
	}
}
