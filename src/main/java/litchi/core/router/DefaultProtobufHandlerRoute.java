//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.router;

import com.google.protobuf.GeneratedMessageV3;
import litchi.core.Litchi;
import litchi.core.exception.ErrorCodeException;
import litchi.core.net.rpc.packet.RequestPacket;
import litchi.core.net.rpc.packet.ResponsePacket;
import litchi.core.net.session.NettySession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * protobuf结构的路由
 *
 * @author 0x737263
 */
public abstract class DefaultProtobufHandlerRoute implements BaseRoute<RequestPacket> {
    protected static final Logger LOGGER = LoggerFactory.getLogger(DefaultProtobufHandlerRoute.class);
    protected static Logger RPC_LOGGER = LoggerFactory.getLogger("rpc");

    private Map<String, Method> parseMethodMaps = new ConcurrentHashMap<>();

    @Override
    public long getThreadHash(NettySession session, RequestPacket packet) {
        return hashByArgsIndex(packet);
    }

    @Override
    public void onReceive(NettySession session, RequestPacket packet) {
        try {

            RouteInfo routeInfo = Litchi.call().route().getRouteInfo(packet.route);
            Object[] parameters = buildParameters(routeInfo, packet);

            if (routeInfo.method.isVoid()) {
                routeInfo.invoke(parameters);
                return;
            }

            Object result = routeInfo.invoke(parameters);
            if (result == null) {
                LOGGER.error("method = {} return type is null", routeInfo.method.getClass());
                return;
            }

            if (result instanceof GeneratedMessageV3 == false) {
                LOGGER.error("method = {} return type error", routeInfo.method.getClass());
                return;
            }

            ResponsePacket rsp = new ResponsePacket();
            rsp.uid = packet.uid;
            rsp.messageId = packet.messageId;
            rsp.route = packet.route;
            rsp.data = ((GeneratedMessageV3) result).toByteArray();
            session.writeRpcPacket(rsp);

        } catch (ErrorCodeException errCode) {
            ResponsePacket rsp = new ResponsePacket();
            rsp.uid = packet.uid;
            rsp.messageId = packet.messageId;
            rsp.route = packet.route;
            rsp.statusCode = errCode.getCode();
            session.writeRpcPacket(rsp);
        } catch (Exception ex) {
            LOGGER.error("", ex);
        } finally {
            if (RPC_LOGGER.isInfoEnabled()) {
                long durationTime = System.currentTimeMillis() - packet.buildTime;
                if (durationTime > 50) {
                    RPC_LOGGER.warn("<---------- [handler]client -> invoke complete. duration time:{}ms ---------->", durationTime);
                    RPC_LOGGER.warn("packet = {}", packet);
                    RPC_LOGGER.warn("<---------- [handler]client -> invoke complete. duration time:{}ms ---------->", durationTime);
                }
            }
        }
    }

    public Object[] buildParameters(RouteInfo routeInfo, RequestPacket packet) {
        try {
            Object[] params = new Object[3];
            params[0] = packet.uid;
            params[1] = packet;  //requestPacket
            params[2] = getParseMethod(routeInfo).invoke(null, packet.args[0]);  //pb
            return params;
        } catch (Exception ex) {
            LOGGER.error("", ex);
        }
        return null;
    }

    /**
     * 从调用函数的参数列表中获取目标序列化对象的解析函数
     * @param routeInfo
     * @return
     */
    public Method getParseMethod(RouteInfo routeInfo) {
        Method parseMethod = parseMethodMaps.get(routeInfo.routeName);
        if(parseMethod == null) {
            try {
                Class<?>[] parameterClazz = routeInfo.method.getJavaMethod().getParameterTypes();
                Class lastClazz = parameterClazz[parameterClazz.length - 1];
                parseMethod = lastClazz.getMethod("parseFrom", byte[].class);
                parseMethodMaps.put(routeInfo.routeName,parseMethod);
            } catch (Exception ex) {
                LOGGER.error("",ex);
            }
        }

        return parseMethod;
    }

}
