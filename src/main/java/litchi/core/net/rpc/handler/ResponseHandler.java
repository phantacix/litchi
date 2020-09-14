//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.rpc.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import litchi.core.Litchi;
import litchi.core.net.rpc.packet.ResponsePacket;
import litchi.core.net.session.GateSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 0x737263
 */
@Sharable
public class ResponseHandler extends BaseChannelHandler<ResponsePacket> {
    private static Logger LOGGER = LoggerFactory.getLogger(ResponseHandler.class);

    private Litchi litchi;

    public ResponseHandler(Litchi litchi) {
        super(ResponsePacket.class);
        this.litchi = litchi;
    }

    @Override
    protected void onChannelRead(ChannelHandlerContext ctx, ResponsePacket packet) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("<--- packet={}", packet);
        }

        GateSession session = litchi.sessionService().getOnlineSession(packet.uid);
        if (session != null) {
            session.writeWebSocketFrame(packet.messageId, packet.route, packet.statusCode, packet.data);
        }
    }
}
