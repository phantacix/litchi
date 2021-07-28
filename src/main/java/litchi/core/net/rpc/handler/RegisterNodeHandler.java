//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.rpc.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.Attribute;
import litchi.core.Litchi;
import litchi.core.event.sys.RpcDisconnectEvent;
import litchi.core.net.rpc.packet.RegisterNodePacket;
import litchi.core.net.session.NettySession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * register node server handler
 *
 * @author 0x737263
 */
@Sharable
public class RegisterNodeHandler extends BaseChannelHandler<RegisterNodePacket> {
    protected static Logger LOGGER = LoggerFactory.getLogger(RegisterNodeHandler.class);
    protected static Logger RPC_LOGGER = LoggerFactory.getLogger("rpc_logger");

    Litchi litchi;

    public RegisterNodeHandler(Litchi litchi) {
        super(RegisterNodePacket.class);
        this.litchi = litchi;
    }

    @Override
    protected void onChannelRead(ChannelHandlerContext ctx, RegisterNodePacket packet) {
        NettySession session = litchi.nodeSessionService().getSession(ctx);
        litchi.nodeSessionService().addSessionNode(packet.nodeType, packet.nodeId, session);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        NettySession nettySession = new NettySession(ctx.channel());
        litchi.nodeSessionService().putSession(nettySession);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        Attribute<Long> attrId = ctx.channel().attr(NettySession.SESSION_ID);
        Long sessionId = attrId.get();
        if (sessionId == null) {
            return;
        }

        try {
            // rpc node disconnect event
            NettySession session = litchi.nodeSessionService().getSession(sessionId);
            String nodeType = session.channel().attr(NettySession.FROM_NODE_TYPE).get();
            String nodeId = session.channel().attr(NettySession.FROM_NODE_ID).get();
            litchi.event().post(new RpcDisconnectEvent(nodeType, nodeId));
        } finally {
            //remove
            litchi.nodeSessionService().removeSession(sessionId);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object e) {
        if (e instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) e;
            if (event.state() == IdleState.ALL_IDLE) {
                RPC_LOGGER.info("channel ALL_IDLE to close. {}", ctx);
                ctx.close();
            }
        }
    }

}