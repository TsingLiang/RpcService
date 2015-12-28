package com.tsing;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class RpcClient extends SimpleChannelInboundHandler<RpcResponse> 
{
    private String host;
    private int port;
    private RpcRequest request; 
    private RpcResponse response;

    public RpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx)
    {
    	System.out.println("client send request.");
    	ctx.writeAndFlush(request);
    }
    
    @Override
    public void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception 
    {
    	System.out.println("client receive: " + response);
        this.response = response;

        synchronized (this) 
        {
            notifyAll();
        }
    }

    public RpcResponse send(RpcRequest request) throws Exception 
    {
        EventLoopGroup group = new NioEventLoopGroup();
        try 
        {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
            		 .channel(NioSocketChannel.class)
            		 .handler(new ChannelInitializer<SocketChannel>() 
            		{
	                    @Override
	                    public void initChannel(SocketChannel channel) throws Exception 
	                    {
	                        channel.pipeline()
	                            .addLast(new RpcEncoder(RpcRequest.class)) 
	                            .addLast(new RpcDecoder(RpcResponse.class)) 
	                            .addLast(RpcClient.this);
	                    }
	                });

            this.request = request; 
            ChannelFuture future = bootstrap.connect(host, port).sync();
            
            System.out.println(future.channel());
            //future.channel().writeAndFlush(request).sync();

            synchronized (this) 
            {
            	System.out.println("client wait response.");
                wait();
            }

            if (response != null) 
            {
                future.channel().closeFuture().sync();
            }
            
            return response;
        } 
        finally 
        {
            group.shutdownGracefully();
        }
    }
}