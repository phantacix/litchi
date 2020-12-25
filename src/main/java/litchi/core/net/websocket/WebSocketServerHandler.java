//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.websocket;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import litchi.core.Litchi;
import litchi.core.dispatch.executor.RequestPacketExecutor;
import litchi.core.event.sys.UserDisconnectEvent;
import litchi.core.net.rpc.packet.RequestPacket;
import litchi.core.net.session.GateSession;
import litchi.core.net.session.GateSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Sharable
public class WebSocketServerHandler extends SimpleChannelInboundHandler<RequestPacket> {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketServerHandler.class);

    private Litchi litchi;
    private GateSessionService sessionService;

    public WebSocketServerHandler(Litchi litchi) {
        this.litchi = litchi;
        this.sessionService = litchi.sessionService();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, RequestPacket packet) {
        GateSession session = sessionService.getSession(ctx);
        if (session == null) {
            LOGGER.warn("session not exist. channelId={}", ctx.channel().id().asLongText());
            ctx.close();
            return;
        }

        if (packet == null) {
            LOGGER.warn("parseRequestPacket is null.channelId={}", ctx.channel().id().asLongText());
            ctx.close();
            return;
        }

        if (!packet.validateRoute()) {
            LOGGER.error("route value is error. route = {}", packet.route);
            return;
        }

        //set uid
        packet.uid = session.uid();

        //如果是本服务器请求则执行，否则，转发到具体的服务器执行
        if (packet.nodeType().equals(litchi.currentNode().getNodeType())) {
            if (litchi.route().getRouteInfo(packet.route) == null) {
                LOGGER.warn("route not found. packet:{}", packet);
                return;
            }

            litchi.dispatch().publish(new RequestPacketExecutor(litchi, session, packet));
            return;
        }

        if (session.uid() < 1) {
            return;
        }

        //forward
        litchi.rpc().forward(session, packet);

    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("channelRegistered: {}", ctx.channel());
        }

        GateSession session = new GateSession(ctx.channel());
        sessionService.putSession(session);
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("channelUnregistered: {}", ctx.channel());
        }
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("channelActive: {}", ctx.channel());
        }

        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("channelInactive: {}", ctx.channel());
        }

        try {
            String channelId = ctx.channel().id().asLongText();
            GateSession session = sessionService.getSession(ctx);
            if (session == null) {
                return;
            }

            //非登陆用户
            if (session.uid() < 1) {
                // 移除session
                LOGGER.debug("if (session.uid() < 1) remove online session");
                sessionService.removeOnlineSession(channelId);
                return;
            }

            boolean online = sessionService.isOnline(session.uid());
            // 移除session
            sessionService.removeOnlineSession(channelId);

            if (online) {
                // 抛出登出事件
                UserDisconnectEvent e = new UserDisconnectEvent(session.uid(), session.getNodeId());
                litchi.event().post(e);
            }
        } catch (Exception e) {
            throw e;
        }

        super.channelInactive(ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent) {
            ctx.close();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Channel idle. {}", ctx.channel().id().asLongText());
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("ip:{} {}", ctx.channel().remoteAddress(), cause.getMessage());
        LOGGER.error("", cause);
    }

}