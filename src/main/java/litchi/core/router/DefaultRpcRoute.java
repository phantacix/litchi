//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.router;

import litchi.core.Litchi;
import litchi.core.common.utils.ServerTime;
import litchi.core.net.rpc.packet.RequestPacket;
import litchi.core.net.rpc.packet.RpcCallbackPacket;
import litchi.core.net.session.NettySession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * rpc服务器路由
 * @author 0x737263
 */
public abstract class DefaultRpcRoute implements BaseRoute<RequestPacket> {
    static final Logger LOGGER = LoggerFactory.getLogger(DefaultProtobufHandlerRoute.class);
    static Logger RPC_LOGGER = LoggerFactory.getLogger("rpc");

    @Override
    public long getThreadHash(NettySession session, RequestPacket packet) {
        return packet.uid;
    }

    @Override
    public void onReceive(NettySession session, RequestPacket packet) {
        try {
            RouteInfo routeInfo = Litchi.call().route().getRouteInfo(packet.route);
            if (routeInfo.method.isVoid()) {
                routeInfo.invoke(packet.args);
                return;
            }

            RpcCallbackPacket rsp = new RpcCallbackPacket();
            rsp.sequenceId = packet.sequenceId;
            rsp.result = routeInfo.invoke(packet.args);
            session.writeRpcPacket(rsp);

        } catch (Exception ex) {
            LOGGER.error("", ex);
        } finally {
            if (RPC_LOGGER.isInfoEnabled()) {
                long durationTime = ServerTime.timeMillis() - packet.buildTime;
                if (durationTime > 50) {
                    RPC_LOGGER.warn("<---------- [rpc]client -> invoke complete. duration time:{}ms ---------->", durationTime);
                    RPC_LOGGER.warn("packet = {}", packet);
                    RPC_LOGGER.warn("<---------- [rpc]client -> invoke complete. duration time:{}ms ---------->", durationTime);
                }
            }
        }
    }
}
