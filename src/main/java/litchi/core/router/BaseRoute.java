//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.router;

import litchi.core.Litchi;
import litchi.core.event.annotation.EventReceive;
import litchi.core.exception.CoreException;
import litchi.core.net.rpc.packet.RequestPacket;
import litchi.core.net.session.NettySession;
import litchi.core.router.annoation.Handler;
import litchi.core.router.annoation.Rpc;
import litchi.core.common.utils.CRCUtils;

/**
 * 路由继承类请配合注解使用
 * handler类型：
 * {@link Handler}
 * rpc 类型：
 * {@link Rpc}
 * event 类型:
 * {@link EventReceive}
 *
 * @author 0x737263
 */
public interface BaseRoute<MSG> {

    /**
     * 获取线程池hash
     * 消息post到派发中心时，需要计算具体在哪个线程中执行
     *
     * @param packet 接收的消息
     * @return
     */
    long getThreadHash(NettySession session, MSG packet);

    /**
     * 收到消息
     *
     * @param packet 接收的消息
     */
    void onReceive(NettySession session, MSG packet);

    default long hashByArgsIndex(RequestPacket packet) {
        RouteInfo routeInfo = Litchi.call().route().getRouteInfo(packet.route);
        if (routeInfo.hashArgsIndex < 0 || routeInfo.parseMethod == null) {
            return packet.uid;
        }

        final Object parameter = packet.getArgs(routeInfo.hashArgsIndex);
        if (parameter == null) {
            throw new CoreException("args index error for invoke method. RouteInfo = %s", routeInfo);
        }
        return CRCUtils.calculateCRC(parameter.toString());
    }

}
