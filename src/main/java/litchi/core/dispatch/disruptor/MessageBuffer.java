//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.dispatch.disruptor;

import litchi.core.dispatch.executor.BaseExecutor;

/**
 * @author 0x737263
 */
public class MessageBuffer {

    private BaseExecutor executor;

    public void setExecutor(BaseExecutor executor) {
        this.executor = executor;
    }

    public void execute() {
        this.executor.invoke();
    }

    public void clear() {
        this.executor = null;
    }

    @Override
    public String toString() {
        return "MessageBuffer{" +
                "executor=" + executor +
                '}';
    }
}
