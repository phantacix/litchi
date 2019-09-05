//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.event.sys;

import litchi.core.event.SysEventKey;
import litchi.core.event.SysEventKey;
import litchi.core.event.UserEvent;

public class UserDisconnectEvent extends UserEvent {

    private String nodeId;

    public UserDisconnectEvent(long uid, String nodeId) {
        super(SysEventKey.ACTOR_DISCONNECT_EVENT, uid);
        this.nodeId = nodeId;
    }

    public String getServerId() {
        return nodeId;
    }

}
