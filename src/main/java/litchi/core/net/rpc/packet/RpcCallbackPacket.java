//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.rpc.packet;

/**
 * 调用rpc返回的消息
 *
 * @author 0x737263
 */
public class RpcCallbackPacket {

    /**
     * 回调序号
     */
    public long sequenceId;

    /**
     * 返回结果
     */
    public Object result;

    @Override
    public String toString() {
        return "RpcCallbackPacket{" +
                "sequenceId=" + sequenceId +
                ", result=" + result +
                '}';
    }
}
