//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.websocket;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
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
public class WebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketHandler.class);

    private Litchi litchi;
    private GateSessionService sessionService;

    public WebSocketHandler(Litchi litchi) {
        this.litchi = litchi;
        this.sessionService = litchi.sessionService();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) {
        GateSession session = sessionService.getSession(ctx);
        if (session == null) {
            LOGGER.warn("session not exist. channelId={}", ctx.channel().id().asShortText());
            ctx.close();
            return;
        }

        RequestPacket packet = parseRequestPacket(session, frame);
        if (packet == null) {
            ctx.close();
            return;
        }

        //如果是本服务器请求则执行，否则，转发到具体的服务器执行
        if (packet.nodeType().equals(litchi.currentNode().getNodeType())) {
            if (litchi.route().getRouteInfo(packet.route) == null) {
                LOGGER.warn("route not found. packet:{}", packet);
                return;
            }

            litchi.dispatch().publish(new RequestPacketExecutor(litchi, session, packet));
            return;
        }

        //如果没登陆则忽略其他提交的消息
        if (session.uid() < 1) {
            return;
        }

        litchi.rpc().forward(session, packet);
    }

    // ----------------------------------------------------------------------------------
    // 服务端[接收]包结构解析 RequestPacket.java
    // messageId	short       2	 	客户端生成的透传消息id(用于客户端回调用)
    // routeLen     byte        1       路由名称长度
    // route        byte[]      n       路由名称
    // bodyLen      short       2       请求的消息数据长度
    // body         byte[]      n       请求的消息数据(结构可使用json、pb等第三方序列库)
    // ----------------------------------------------------------------------------------
    // 服务端[响应]包结构解析 ResponsePacket.java
    // messageId	short       2       客户端的透传消息id(原路返回)
    // routeLen     byte        1       路由名称长度
    // route        byte[]      n       路由名称
    // statusCode   short       2       状态码（详情:StatusCode.java)，如果不为0则没有响应的消息数据
    // dataLen      short       2       响应的消息数据长度
    // data         byte[]      n       响应的消息数据(结构可使用json、pb等第三方序列库)
    // ----------------------------------------------------------------------------------

    /**
     * messageId(2) routeLen(1) route[routeLen] bodyLen(2) body[bodyLen]
     * @param session
     * @param frame
     * @return
     */
    public RequestPacket parseRequestPacket(GateSession session, WebSocketFrame frame) {
        if (frame instanceof BinaryWebSocketFrame) {
            RequestPacket requestPacket = RequestPacket.valueOfHandler(frame, session.uid());
            if (!requestPacket.validateRoute()) {
                LOGGER.error("route value is error. route = {}", requestPacket.route);
                return null;
            }
            return requestPacket;
        }

        LOGGER.error("unsupported frame type: {}", frame.getClass().getName());
        return null;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Connected: {}", ctx.channel());
        }

        GateSession session = new GateSession(ctx.channel());
        sessionService.putSession(session);
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        try {
            String channelId = ctx.channel().id().asShortText();
            GateSession session = sessionService.getSession(ctx);
            if (session == null) {
                return;
            }

            //非登陆用户
            if (session.uid() < 1) {
                // 移除session
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
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent) {
            ctx.close();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Channel idle. {}", ctx.channel().id().asShortText());
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("ip:{} {}", ctx.channel().remoteAddress(), cause.getMessage());
        LOGGER.error("", cause);
    }

}