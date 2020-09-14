//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.dispatch.executor;

import litchi.core.dispatch.disruptor.MessageThread;
import litchi.core.net.rpc.client.RpcCallbackContext;
import litchi.core.net.rpc.packet.RpcCallbackPacket;

import java.util.List;

/**
 * @author 0x737263
 */
public class RpcCallbackPacketExecutor implements BaseExecutor {

    RpcCallbackPacket packet;

    RpcCallbackContext callbackContext;

    public RpcCallbackPacketExecutor(RpcCallbackContext callbackContext, RpcCallbackPacket packet) {
        this.callbackContext = callbackContext;
        this.packet = packet;
    }

    @Override
    public int threadId() {
        return callbackContext.threadId;
    }

    @Override
    public int calcHash(List<MessageThread> list) {
        return (int) (callbackContext.hash % list.size());
    }

    @Override
    public void invoke() {
        callbackContext.rpcCallback.result(packet.result);
    }
}
