package com.focusit.netty.server;

import com.focusit.netty.client.Sample;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by Denis V. Kirpichenkov on 30.12.14.
 */
public class SampleDecoder extends MessageToMessageDecoder<ByteBuf> {
	private final Charset charset = Charset.forName("UTF-8");

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
		long appId = msg.readLong();
		int dataSize = msg.readInt();
		byte data[] = new byte[dataSize];
		msg.readBytes(data);

		out.add(new Sample(appId, new String(data, charset)));
	}
}
