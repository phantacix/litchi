//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.event.sys;

import litchi.core.event.GameEvent;
import litchi.core.event.SysEventKey;
import litchi.core.event.GameEvent;
import litchi.core.event.SysEventKey;

public class RpcDisconnectEvent extends GameEvent {
	
	private String serverType;
	
	private String serverId;

	public RpcDisconnectEvent(String serverType, String serverId) {
		super(SysEventKey.RPC_DISCONNECT_EVENT);
		this.serverType = serverType;
		this.serverId = serverId;
	}

	@Override
	public long dispatchHash() {
		return 0;
	}
	
	public String getServerId() {
		return serverId;
	}
	
	public String getServerType() {
		return serverType;
	}

}
