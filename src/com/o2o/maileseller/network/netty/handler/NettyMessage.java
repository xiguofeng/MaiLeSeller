package com.o2o.maileseller.network.netty.handler;

public interface NettyMessage {

	Integer getMessageType();
	
	String getMessageBody();
}
