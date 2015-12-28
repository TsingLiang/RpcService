package com.tsing;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcEncoder extends MessageToByteEncoder 
{
    private Class<?> clazz;

    public RpcEncoder(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception 
    {
    	System.out.println("encode: " + in);
        if (clazz.isInstance(in)) 
        {
            byte[] bytes = SerializationUtil.serialize(in);
            out.writeInt(bytes.length);
            out.writeBytes(bytes);
        }
    }
}
