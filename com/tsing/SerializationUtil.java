package com.tsing;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

public class SerializationUtil 
{
	private static Map<Class<?>, Schema<?>> schemas = new ConcurrentHashMap<Class<?>, Schema<?>>();
	
	private static <T> Schema<T> getSchema(Class<?> clazz)
	{
		Schema<T> schema = (Schema<T>) schemas.get(clazz);
		if(schema == null)
		{
			schema = (Schema<T>) RuntimeSchema.createFrom(clazz);
			if(schema != null)
				schemas.put(clazz, schema);
			
		}
		
		return schema;
	}
	
	public static <T> byte[] serialize(T obj)
	{
		System.out.println("serialize.");
		Class<?> clazz = obj.getClass();
		Schema<T> schema = getSchema(clazz);
		LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE); 
		
		return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
	}
	
	public static <T> T deserialize(byte[] bytes, Class<?> clazz) throws Exception
	{
		System.out.println("deserialize.");
		T obj = (T) clazz.newInstance();
		Schema<T> schema = getSchema(clazz);
		ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
		
		return obj;
	}
}
