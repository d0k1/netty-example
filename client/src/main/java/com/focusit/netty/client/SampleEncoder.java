package com.focusit.netty.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by Denis V. Kirpichenkov on 30.12.14.
 */
public class SampleEncoder extends MessageToMessageEncoder<Sample> {
	private final Charset charset = Charset.forName("UTF-8");
	@Override
	protected void encode(ChannelHandlerContext ctx, Sample msg, List<Object> out) throws Exception {
		byte data[] = msg.data.getBytes(charset);
		ByteBuf buffer = ctx.alloc().heapBuffer(data.length+8+4);
		buffer.writeLong(msg.appId);
		buffer.writeInt(data.length);
		buffer.writeBytes(data);
		out.add(buffer);
	}
}
