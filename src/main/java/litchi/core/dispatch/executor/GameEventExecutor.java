//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.dispatch.executor;

import litchi.core.Litchi;
import litchi.core.dispatch.disruptor.MessageThread;
import litchi.core.event.GameEvent;

import java.util.List;

/**
 * @author 0x737263
 */
public class GameEventExecutor implements BaseExecutor {

    Litchi litchi;
    GameEvent event;

    public GameEventExecutor(Litchi litchi, GameEvent event) {
        this.litchi = litchi;
        this.event = event;
    }

    @Override
    public int threadId() {
        return event.threadId;
    }

    @Override
    public int calcHash(List<MessageThread> list) {
        return (int) this.event.dispatchHash() % list.size();
    }

    @Override
    public void invoke() {
        litchi.event().execute(event);
    }
}
