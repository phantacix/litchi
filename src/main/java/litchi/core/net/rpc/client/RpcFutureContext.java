//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.rpc.client;

import litchi.core.net.rpc.packet.RpcCallbackPacket;
import litchi.core.net.rpc.packet.RpcCallbackPacket;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class RpcFutureContext {

    /**
     * key:sequenceId, value:{key:RpcCallback<?>, value:threadId }
     */
    private Map<Long, RpcCallbackContext> callbackMaps = new ConcurrentSkipListMap<>();

    /**
     * key:sequenceId, value:RpcFuture<RpcCallbackPacket>
     */
    private Map<Long, RpcFuture<RpcCallbackPacket>> futureMaps = new ConcurrentSkipListMap<>();


    public RpcFutureContext() {

    }

    public void setCallback(long sequenceId, RpcCallbackContext rpcCallbackContext) {
        callbackMaps.put(sequenceId, rpcCallbackContext);
    }

    public RpcCallbackContext getCallback(long sequenceId) {
        return callbackMaps.remove(sequenceId);
    }


    public void setRpcFuture(long sequenceId, RpcFuture<RpcCallbackPacket> future) {
        futureMaps.put(sequenceId, future);
    }

    public void notifyRpcMessage(RpcCallbackPacket packet) {
        RpcFuture<RpcCallbackPacket> future = futureMaps.remove(packet.sequenceId);
        if (future != null) {
            future.setResponse(packet);
        }
    }
}
