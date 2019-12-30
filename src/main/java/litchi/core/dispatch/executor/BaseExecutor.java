//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.dispatch.executor;

import litchi.core.dispatch.disruptor.MessageThread;

import java.util.List;

/**
 * dispatch to target thread executor
 *
 * @author 0x737263
 */
public interface BaseExecutor {

    int threadId();

    int calcHash(List<MessageThread> list);

    void invoke();
}
