package com.o2o.maileseller.network.netty.handler;

import io.netty.channel.ChannelHandlerContext;

public interface NettyMessageHandler {

	void handleNettyMessage(ChannelHandlerContext ctx, NettyMessage nettyMessage);
}
