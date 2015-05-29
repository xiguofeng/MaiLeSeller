package com.o2o.maileseller.network.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import android.util.Log;

import com.o2o.maileseller.network.netty.codec.JsonUtil;

public abstract class AbstractNettyMessageHandler<T> implements
		NettyMessageHandler {

	public void handleNettyMessage(ChannelHandlerContext ctx,
			NettyMessage nettyMessage) {
		Log.e("xxx_nettyMessage.getMessageBody()",
				nettyMessage.getMessageBody());
		Class<T> clazz = getRequestType();
		Log.e("xxx_clazz", "" + clazz.getName());
		try {
			if (clazz.equals(String.class)) {
				handle(ctx, (T) nettyMessage.getMessageBody());
				Log.e("xxx_if", "xxx_if");
			} else if (null == nettyMessage.getMessageBody()
					|| "".equals(nettyMessage.getMessageBody())) {
				handle(ctx, null);
				Log.e("xxx_else if ", "xxx_if");
			} else {
				T request = JsonUtil.jsonToBean(nettyMessage.getMessageBody(),
						clazz);
				Log.e("xxx_else", "xxx_if");
				handle(ctx, request);
			}

		} catch (Exception e) {
			Log.e("xxx_Exception", "" + e.getMessage());
		}
	}

	protected abstract void handle(ChannelHandlerContext ctx, T request);

	protected abstract Class<T> getRequestType();
}
