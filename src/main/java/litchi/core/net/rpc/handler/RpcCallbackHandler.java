//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.rpc.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import litchi.core.Litchi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import litchi.core.dispatch.executor.RpcCallbackPacketExecutor;
import litchi.core.net.rpc.packet.RpcCallbackPacket;
import litchi.core.net.rpc.client.RpcCallbackContext;
import litchi.core.net.rpc.client.RpcFutureContext;

/**
 * @author 0x737263
 */
@Sharable
public class RpcCallbackHandler extends BaseChannelHandler<RpcCallbackPacket> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcCallbackHandler.class);
    protected Logger RPC_LOGGER = LoggerFactory.getLogger("remote");

    private RpcFutureContext futureContext;
    private Litchi litchi;

    public RpcCallbackHandler(Litchi litchi, RpcFutureContext futureContext) {
        super(RpcCallbackPacket.class);
        this.litchi = litchi;
        this.futureContext = futureContext;
    }

    @Override
    protected void onChannelRead(ChannelHandlerContext ctx, RpcCallbackPacket packet) {
        if (RPC_LOGGER.isDebugEnabled()) {
            RPC_LOGGER.debug("<----- receive msg. {}", packet);
        }

        //TODO 这里是性能瓶颈
        RpcCallbackContext callbackContext = futureContext.getCallback(packet.sequenceId);
        if (callbackContext != null) {
            litchi.dispatch().publish(new RpcCallbackPacketExecutor(callbackContext, packet));
        } else {
            futureContext.notifyRpcMessage(packet);
        }
    }

//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) {
//        LOGGER.error("----- nodeType = {}, nodeId = {} connect closed.", nodeType, nodeId);
//        LOGGER.error("{}", throwable.getMessage());
//        ctx.close();
//    }
}
