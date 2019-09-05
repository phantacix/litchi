//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.rpc.client;

/**
 * @author 0x737263
 */
public class RpcCallbackContext {

    public int threadId;

    public long hash;

    public RpcCallback rpcCallback;

    public RpcCallbackContext(int threadId, long hash, RpcCallback rpcCallback) {
        this.threadId = threadId;
        this.hash = hash;
        this.rpcCallback = rpcCallback;
    }
}
