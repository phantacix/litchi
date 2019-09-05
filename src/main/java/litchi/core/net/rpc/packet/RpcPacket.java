//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.rpc.packet;

/**
 * rpc packet shell
 *
 * @author 0x737263
 */
public class RpcPacket<T> {

    public T data;

    public RpcPacket() {
    }

    public RpcPacket(T data) {
        this.data = data;
    }
}
