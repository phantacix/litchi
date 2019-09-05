//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import litchi.core.net.rpc.RpcConfig;
import litchi.core.net.rpc.packet.RpcPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import litchi.core.net.rpc.packet.RpcPacket;
import litchi.core.net.rpc.RpcConfig;

/**
 * encode rpc packet
 *
 * @author 0x737263
 */
public class RpcEncoder extends MessageToByteEncoder<Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcEncoder.class);

    private Class<?> genericClass;

    public RpcEncoder() {
        this.genericClass = RpcPacket.class;
    }

    public RpcEncoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
        if (this.genericClass == in.getClass()) {
            byte[] data = RpcConfig.getSerializer().encode(in);
            out.writeInt(data.length);
            out.writeBytes(data);
        } else {
            LOGGER.warn("packet type error. packet={}", in);
        }
    }
}