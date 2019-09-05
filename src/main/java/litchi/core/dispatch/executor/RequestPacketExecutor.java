//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.dispatch.executor;

import litchi.core.Litchi;
import litchi.core.dispatch.disruptor.MessageThread;
import litchi.core.net.rpc.packet.RequestPacket;
import litchi.core.net.session.NettySession;
import litchi.core.router.RouteInfo;

import java.util.List;

/**
 * @author 0x737263
 */
public class RequestPacketExecutor implements BaseExecutor {

    Litchi litchi;
    NettySession session;
    RequestPacket packet;

    public RequestPacketExecutor(Litchi litchi, NettySession session, RequestPacket packet) {
        this.litchi = litchi;
        this.session = session;
        this.packet = packet;
    }

    @Override
    public int threadId() {
        return litchi.route().getThreadId(packet.route);
    }

    @Override
    public int calcHash(List<MessageThread> list) {
        RouteInfo routeInfo = litchi.route().getRouteInfo(packet.route);
        long hash = routeInfo.instance.getThreadHash(session, packet);
        return (int) (hash % list.size());
    }

    @Override
    public void invoke() {
        RouteInfo routeInfo = litchi.route().getRouteInfo(packet.route);
        routeInfo.instance.onReceive(session, packet);
    }
}
