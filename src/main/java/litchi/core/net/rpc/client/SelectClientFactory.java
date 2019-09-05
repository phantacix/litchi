//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.rpc.client;

import litchi.core.net.session.NettySession;
import litchi.core.net.session.NettySession;

/**
 * 选择RPC客户端
 *
 * @author 0x737263
 */
public interface SelectClientFactory<T> {

    /**
     * 根据不同类型的服务器进行不同的RpcClient选择
     *
     * @param session
     * @param request
     * @return
     */
    NettyRpcClient select(NettySession session, String nodeType, T request);
}
