package com.o2o.maileseller.network.netty.client;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.concurrent.TimeUnit;

import com.o2o.maileseller.network.netty.NettyMessageType;
import com.o2o.maileseller.network.netty.handler.NettyMessage;
import com.o2o.maileseller.network.netty.handler.NettyMessageImpl;
import com.o2o.maileseller.util.TimeUtils;
import com.o2o.maileseller.util.file.FileUtil;

@Sharable
public class ClientConnectMonitorHandler extends
		SimpleChannelInboundHandler<NettyMessage> {

	/**
	 * 连接通道是否被关闭
	 */
	boolean channelClosed = true;

	public ClientConnectMonitorHandler() {

	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		channelClosed = false;

		println("channelActive: " + ctx.channel().remoteAddress());
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, NettyMessage msg)
			throws Exception {
		if (msg.getMessageType().intValue() == NettyMessageType.Ping.getType()) {
			// Discard
			println("Ping message received...");
		} else {
			ctx.fireChannelRead(msg);
		}
	}

	@Override
	public void userEventTriggered(final ChannelHandlerContext ctx, Object evt) {
		if (!(evt instanceof IdleStateEvent)) {
			return;
		}

		IdleStateEvent e = (IdleStateEvent) evt;
		if (e.state() == IdleState.READER_IDLE) {

			println("Not receive msg for long time, close channel.");
			closeChannel(ctx);
		} else if (e.state() == IdleState.WRITER_IDLE) {
			final EventLoop loop = ctx.channel().eventLoop();
			loop.schedule(new Runnable() {
				@Override
				public void run() {
					println("Write ping info...");
					NettyMessage nettyMessage = new NettyMessageImpl(
							NettyMessageType.Ping.getType(), "ping");
					boolean result = false;
					while (!result) {
						ChannelFuture f = ctx.writeAndFlush(nettyMessage);
						try {
							f.sync();

							result = f.isSuccess() && null == f.cause();
						} catch (InterruptedException e) {
							println(e.toString());
						}
					}

				}
			}, 0, TimeUnit.SECONDS);

		}
	}

	@Override
	public void channelInactive(final ChannelHandlerContext ctx) {
		println("Disconnected from: " + ctx.channel().remoteAddress());
		closeChannel(ctx);
	}

	@Override
	public void channelUnregistered(final ChannelHandlerContext ctx)
			throws Exception {
		println("channelUnregistered...");
		closeChannel(ctx);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		println("exceptionCaught..." + cause.toString());
		closeChannel(ctx);
	}

	void println(final String msg) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				FileUtil.write(TimeUtils.getCurrentTime() + ":" + msg);
			}
		}).start();

		System.out.println("XXX.CONNECT: " + msg);
	}

	void closeChannel(ChannelHandlerContext ctx) {
		if (!channelClosed) {
			ctx.close();
			channelClosed = true;
		}

	}
}
