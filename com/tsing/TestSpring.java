package com.tsing;

import java.util.Map;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestSpring 
{
	@Test
	public void testComponent()
	{
		ApplicationContext ctx = new ClassPathXmlApplicationContext("/com/tsing/beans.xml");
		Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);
		
		System.out.println("hello");
		System.out.println(serviceBeanMap);
	}
}
