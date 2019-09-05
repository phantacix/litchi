//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.rpc.packet;

import litchi.core.common.utils.ServerTime;
import litchi.core.common.utils.ServerTime;

/**
 * @author 0x737263
 */
public class HeartbeatPacket {

    public String fromNodeType;
    public String fromNodeId;
    public long time;

    public HeartbeatPacket(String nodeType, String nodeId) {
        this.fromNodeType = nodeType;
        this.fromNodeId = nodeId;
        this.time = ServerTime.timeMillis();
    }

    @Override
    public String toString() {
        return "HeartbeatPacket{" +
                "fromNodeType='" + fromNodeType + '\'' +
                ", fromNodeId='" + fromNodeId + '\'' +
                ", time=" + time +
                '}';
    }
}
