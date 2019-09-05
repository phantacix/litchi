//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import litchi.core.net.rpc.RpcConfig;
import litchi.core.net.rpc.packet.RpcPacket;
import litchi.core.net.rpc.packet.RpcPacket;
import litchi.core.net.rpc.RpcConfig;

import java.util.List;

/**
 * decode rpc packet
 *
 * @author 0x737263
 */
public class RpcDecoder extends ByteToMessageDecoder {
	
	private Class<?> genericClass;

	public RpcDecoder() {
		this.genericClass = RpcPacket.class;
	}

	public RpcDecoder(Class<?> genericClass) {
		this.genericClass = genericClass;
	}

	@Override
	public final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() < 4) {
			return;
		}
		in.markReaderIndex();
		int dataLength = in.readInt();

		if (in.readableBytes() < dataLength) {
			in.resetReaderIndex();
			return;
		}
		byte[] data = new byte[dataLength];
		in.readBytes(data);

		Object obj = RpcConfig.getSerializer().decode(data, genericClass);
		out.add(obj);
	}

}
