//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.rpc.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import litchi.core.Litchi;
import litchi.core.dispatch.executor.RequestPacketExecutor;
import litchi.core.net.rpc.packet.RequestPacket;
import litchi.core.net.session.NettySession;

/**
 * @author 0x737263
 */
@Sharable
public class RequestHandler extends BaseChannelHandler<RequestPacket> {

    private Litchi litchi;

    public RequestHandler(Litchi litchi) {
        super(RequestPacket.class);
        this.litchi = litchi;
    }

    @Override
    protected void onChannelRead(ChannelHandlerContext ctx, RequestPacket packet) {
        NettySession session = litchi.nodeSessionService().getSession(ctx);
        litchi.dispatch().publish(new RequestPacketExecutor(litchi, session, packet));
    }
}
