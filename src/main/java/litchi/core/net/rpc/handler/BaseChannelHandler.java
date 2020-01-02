//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.rpc.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import litchi.core.net.rpc.packet.RpcPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 0x737263
 */
public abstract class BaseChannelHandler<T> extends SimpleChannelInboundHandler<RpcPacket<T>> {
    protected static Logger LOGGER = LoggerFactory.getLogger(BaseChannelHandler.class);
    private Class<T> clazz;

    public BaseChannelHandler(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcPacket<T> packet) throws Exception {
        if (clazz.isAssignableFrom(packet.data.getClass())) {
            onChannelRead(ctx, packet.data);
        } else {
            ctx.fireChannelRead(packet);
        }
    }

    /**
     * read data packet and process
     * @param ctx
     * @param packet
     */
    protected abstract void onChannelRead(ChannelHandlerContext ctx, T packet);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        try {
            LOGGER.error("server connect is closed. ctx={}", ctx);
            LOGGER.error("{} ", cause.getMessage());
        } finally {
            ctx.close();
        }
    }
}
