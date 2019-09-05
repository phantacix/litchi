//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.rpc.packet;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

/**
 * request packet for game client
 *
 * @author 0x737263
 */
public class RequestPacket {
    private static AtomicLong sequenceBuilder = new AtomicLong();

    public long uid;

    public long sequenceId;

    public short messageId;

    /**
     * nodeType.className.methodName
     */
    public String route = "";

    /**
     * parameters arrays
     */
    public Object[] args;

    public long buildTime;

    public RequestPacket() {
        this.sequenceId = sequenceBuilder.getAndIncrement();
        this.buildTime = System.currentTimeMillis();
    }

    public static RequestPacket valueOfRPC(String nodeType, String className, String methodName, Object[] args) {
        RequestPacket request = new RequestPacket();
        request.route = String.join(".", nodeType, className, methodName);
        request.args = args;
        return request;
    }

//    public static RequestPacket valueOfRPC(String route, Object[] args) {
//        RequestPacket request = new RequestPacket();
//        request.route = route;
//        request.args = args;
//        return request;
//    }

    public static RequestPacket valueOfHandler(short messageId, String route, long uid, byte[] data) {
        RequestPacket request = new RequestPacket();
        request.uid = uid;
        request.messageId = messageId;
        request.route = route;
        request.args = new Object[1];
        request.args[0] = data;
        return request;
    }

//    public static String buildRoute(String nodeType, Class<?> clazz, String methodName) {
//        return String.join(".", nodeType, clazz.getSimpleName(), methodName);
//    }

    public Object getArgs(int index) {
        if (this.args == null) {
            return null;
        }
        if (this.args.length < index) {
            return null;
        }
        return this.args[index];
    }

    /**
     * 目标执行的服务器类型
     *
     * @return
     */
    public String nodeType() {
        String[] routeIds = route.split("\\.");
        return routeIds[0];
    }

    @Override
    public String toString() {
        return "RequestPacket{" +
                "sequenceId=" + sequenceId +
                ", messageId=" + messageId +
                ", route='" + route + '\'' +
                ", args=" + Arrays.toString(args) +
                ", timestamp=" + buildTime +
                '}';
    }
}
