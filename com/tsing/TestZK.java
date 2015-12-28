package com.tsing;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class TestZK 
{ 
	public static void main(String[] args) throws Exception
	{
		ZooKeeper zk = new ZooKeeper("127.0.0.1:2181", 5000, new Watcher()
		{
			public void process(WatchedEvent event)
			{
				System.out.println("event type : " + event.getState());
			}
		});
		
		Thread.sleep(10 * 1000);
		
		System.out.println("over!");
	}
}
