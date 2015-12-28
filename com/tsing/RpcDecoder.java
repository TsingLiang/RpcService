package com.tsing;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class RpcDecoder extends ByteToMessageDecoder 
{
    private Class<?> clazz;

    public RpcDecoder(Class<?> clazz) 
    {
        this.clazz = clazz;
    }

    @Override
    public final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception 
    {
        if (in.readableBytes() < 4) 
        {
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (dataLength < 0) 
        {
            ctx.close();
        }
        if (in.readableBytes() < dataLength) 
        {
            in.resetReaderIndex();
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);

        Object obj = SerializationUtil.deserialize(data, clazz);
        out.add(obj);
        
        System.out.println("decode: " + obj);
    }
}