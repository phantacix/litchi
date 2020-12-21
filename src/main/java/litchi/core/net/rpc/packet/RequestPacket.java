//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.rpc.packet;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import litchi.core.common.utils.StringUtils;

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

    public static RequestPacket valueOfHandler(WebSocketFrame frame, long uid) {
        RequestPacket request = new RequestPacket();

        //uid
        request.uid = uid;

        //messageId
        ByteBuf message = frame.content();
        request.messageId = message.readShort();

        //route
        byte[] routeBytes = new byte[message.readByte()];
        message.readBytes(routeBytes);

        request.route = new String(routeBytes);

        //data
        byte[] data = new byte[message.readShort()];
        message.readBytes(data);
        request.args = new Object[1];
        request.args[0] = data;

//			long crc = message.readLong();
//			byte[] array = message.array();
//			//数据包正确性验证
//			if (crc != CRCUtils.calculateCRC(Parameters.CRC32, array, 0, array.length - 8)) {
//				LOGGER.error("request packet crc error. crc={} array={}", crc, Arrays.toString(array));
//				return;
//			}

        return request;
    }

    public boolean validateRoute() {
        if (StringUtils.isBlank(this.route)) {
            return false;
        }

        if (this.route.split("\\.").length != 3) {
            return false;
        }
        return true;
    }

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
