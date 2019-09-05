//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.rpc.packet;

/**
 * 结点注册数据包
 *
 * @author 0x737263
 */
public class RegisterNodePacket {

    /**
     * 结点类型
     */
    public String nodeType;

    /**
     * 结点id
     */
    public String nodeId;

    @Override
    public String toString() {
        return "RegisterNodePacket{" +
                "nodeType='" + nodeType + '\'' +
                ", nodeId='" + nodeId + '\'' +
                '}';
    }
}
