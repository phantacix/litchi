//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.dispatch;

import litchi.core.Constants;
import litchi.core.Litchi;
import litchi.core.dispatch.disruptor.ThreadInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import litchi.core.dispatch.disruptor.DisruptorService;
import litchi.core.dispatch.executor.BaseExecutor;

import java.util.List;

/**
 * 默认的消息派发中心实现
 *
 * @author 0x737263
 */
public class DispatcherComponent implements Dispatcher {
    private static Logger LOGGER = LoggerFactory.getLogger(DispatcherComponent.class);

    DisruptorService disruptorService;

    protected Litchi litchi;

    public DispatcherComponent(Litchi litchi, List<ThreadInfo> threadList) {
        this(litchi, 65536, threadList);
    }

    public DispatcherComponent(Litchi litchi, int ringBufferSize, List<ThreadInfo> threadList) {
        this.litchi = litchi;
        this.disruptorService = new DisruptorService(litchi, ringBufferSize);
        this.disruptorService.addThreads(threadList);
    }

    @Override
    public String name() {
        return Constants.Component.DISPATCH;
    }

    @Override
    public void start() {
        this.disruptorService.start();
        LOGGER.info("dispatch init complete!");
    }

    @Override
    public void afterStart() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void publish(BaseExecutor executor) {
        this.disruptorService.publish(executor);
    }

    @Override
    public boolean isEmpty(int threadId) {
        return this.disruptorService.isEmpty(threadId);
    }

    @Override
    public void addThread(String name, int threadId, int threadNum) {
        this.disruptorService.addThread(name, threadId, threadNum);
    }
}
