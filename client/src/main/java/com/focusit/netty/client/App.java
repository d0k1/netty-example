package com.focusit.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.WrappedByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.SocketAddress;

/**
 * Created by Denis V. Kirpichenkov on 28.12.14.
 */
public class App {
	static EventLoopGroup workerGroup = new NioEventLoopGroup();
	static ChannelFuture f;
	static Channel channel;
	static ChannelFuture lastWrite = null;

	public static void main(String[] args) throws InterruptedException {
		Bootstrap b = new Bootstrap(); // (1)
		b.group(workerGroup); // (2)
		b.channel(NioSocketChannel.class); // (3)
		b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
		b.option(ChannelOption.TCP_NODELAY, true);
		b.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(getHandler());
			}
		});

		// Start the client.
		f = b.connect("127.0.0.1", 16000).sync(); // (5)
		channel = f.await().channel();
		Thread beat = new Thread(new Runnable() {
			@Override
			public void run() {
				while(!Thread.interrupted()){
					for(int i=0;i<10;i++) {
						try {
							if (channel.isWritable()){
								ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer();
								buffer.writeBytes("123".getBytes());
								lastWrite = channel.write(buffer);
							}
							Thread.sleep(200);

						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					channel.flush();
//					if(lastWrite!=null) {
//						try {
//							lastWrite.sync();
//							lastWrite = null;
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//					}
				}
			}
		});
		beat.start();
	}

	public static ChannelHandler getHandler(){
		return new ChannelHandlerAdapter(){
			@Override
			public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
				System.err.println("error: "+cause.toString());
			}

			@Override
			public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
				System.err.println("Connect");
				super.connect(ctx, remoteAddress, localAddress, promise);
			}

			@Override
			public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
				System.err.println("Disconnect");
				super.disconnect(ctx, promise);
			}

			@Override
			public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
				System.err.println("Close");
				super.close(ctx, promise);
			}

			@Override
			public void read(ChannelHandlerContext ctx) throws Exception {
				System.err.println("Read");
				super.read(ctx);
			}

			@Override
			public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
				System.err.println("Write");
				super.write(ctx, msg, promise);
			}

			@Override
			public void flush(ChannelHandlerContext ctx) throws Exception {
				System.err.println("Flush");
				super.flush(ctx);
			}
		};
	}
}
