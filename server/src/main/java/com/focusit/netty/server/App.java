package com.focusit.netty.server;

import com.focusit.netty.client.Sample;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

/**
 * Created by Denis V. Kirpichenkov on 28.12.14.
 */
public class App {
	private static ChannelFuture f;


	public static void main(String[] args) {
		EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap(); // (2)
			b.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class) // (3)
				.childHandler(new ChannelInitializer<SocketChannel>() { // (4)
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(getDecoder(), getHandler());
					}
				})
				.option(ChannelOption.SO_BACKLOG, 128)          // (5)
				.childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

			// Bind and start to accept incoming connections.
			f = b.bind(16000).sync(); // (7)

			// Wait until the server socket is closed.
			// In this example, this does not happen, but you can do that to gracefully
			// shut down your server.
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		workerGroup.shutdownGracefully();
		bossGroup.shutdownGracefully();
	}

	public static ChannelHandler getDecoder(){
		return new SampleDecoder();
	}

	public static ChannelHandler getHandler(){
		return new ChannelHandlerAdapter() { // (1)

			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
				System.err.println((Sample)msg);
				// Discard the received data silently.
				ReferenceCountUtil.release(msg);
			}

			@Override
			public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
				// Close the connection when an exception is raised.
				cause.printStackTrace();
				ctx.close();
			}
		};
	}
}
