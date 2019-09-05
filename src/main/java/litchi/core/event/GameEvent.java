//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.event;

/**
 * 游戏相关事件
 *
 * @author 0x737263
 */
public abstract class GameEvent implements Cloneable {

    public int threadId;

    /**
     * 事件的key {@code EventKey}
     */
    public String name;

    public GameEvent() {
    }

    public GameEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    /**
     * DispatchType用于hash线程
     */
    public abstract long dispatchHash();

    @SuppressWarnings("unchecked")
    public <T> T convert() {
        return (T) this;
    }


    @Override
    public String toString() {
        return "GameEvent [name=" + name + ", UniqueId=" + dispatchHash() + "]";
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
