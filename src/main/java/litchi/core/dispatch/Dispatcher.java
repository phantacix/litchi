//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.dispatch;

import litchi.core.components.Component;
import litchi.core.components.Component;
import litchi.core.dispatch.executor.BaseExecutor;

/**
 * 派发器接口
 *
 * @author 0x737263
 */
public interface Dispatcher extends Component {

    void publish(BaseExecutor executor);

    boolean isEmpty(int threadId);

    void addThread(String name, int threadId, int threadNum);
}
