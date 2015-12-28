package com.tsing;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event;
import org.apache.zookeeper.data.Stat;

public class ServiceRegistry 
{
	private static final int SESSION_TIMEOUT = 5000;
	private static final String REGISTRY_PATH = "/registry";
	
	private CountDownLatch latch; 
	private String registryAddress;
	
	public ServiceRegistry(String registryAddress)
	{
		this.registryAddress = registryAddress;
	}
	
	public void registry(String service, String address)
	{
		try
		{
			ZooKeeper zk = connectServer();
			System.out.println(zk);
			Stat stat = zk.exists(REGISTRY_PATH + "/" + service, false);
			if (stat == null)
			{
				createNode(zk, service);
			}
			
			createNode(zk, service + "/" + address);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
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
	
	private void createNode(ZooKeeper zk, String node)
	{
		try
		{
			zk.create(REGISTRY_PATH + "/" + node, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		ServiceRegistry registry = new ServiceRegistry("127.0.0.1:2181");
		registry.registry("Hello", "127.0.0.1:9000");
		System.out.println(registry);
		Thread.sleep(1000000);
	}
}