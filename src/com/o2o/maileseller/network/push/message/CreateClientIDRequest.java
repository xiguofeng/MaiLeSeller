package com.o2o.maileseller.network.push.message;

import com.o2o.maileseller.network.netty.NettyMessageType;
import com.o2o.maileseller.network.netty.handler.NettyMessage;
import com.o2o.maileseller.network.netty.handler.NettyMessageImpl;

public class CreateClientIDRequest {
	
	public static NettyMessage encode()
	{
		NettyMessage nettyMessage = new NettyMessageImpl(NettyMessageType.CreateClientID.getType(), "");
		return nettyMessage;
	}

	
	
}
