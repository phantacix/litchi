//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.event;

/**
 * 角色相关事件
 * @author PhilShen
 *
 */
public abstract class UserEvent extends GameEvent {

	/**
	 * uid
	 */
	private long uid;

	//ThreadId.ACTOR
	public UserEvent(String name, long uid) {
		super(name);
		this.uid = uid;
	}

	@Override
	public long dispatchHash() {
		return uid;
	}

	public long getUid() {
		return uid;
	}

}
