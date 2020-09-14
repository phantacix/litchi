//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.event.sys;

import litchi.core.event.GameEvent;
import litchi.core.event.SysEventKey;

public class RpcDisconnectEvent extends GameEvent {

    private String nodeType;

    private String nodeId;

    public RpcDisconnectEvent(String nodeType, String nodeId) {
        super(SysEventKey.RPC_DISCONNECT_EVENT);
        this.nodeType = nodeType;
        this.nodeId = nodeId;
    }

    @Override
    public long dispatchHash() {
        return 0;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getNodeType() {
        return nodeType;
    }

}
