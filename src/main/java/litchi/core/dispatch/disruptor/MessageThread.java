//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.dispatch.disruptor;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import litchi.core.Litchi;
import litchi.core.common.thread.NamedThreadFactory;
import litchi.core.dispatch.executor.BaseExecutor;

/**
 * @author 0x737263
 */
public class MessageThread {

    private Disruptor<MessageBuffer> disruptor;

    public MessageThread(Litchi litchi, String threadName, int bufferSize) {
        this(threadName, bufferSize, new MessageEventHandler(litchi));
    }

    public MessageThread(String threadName, int bufferSize, EventHandler<MessageBuffer> handler) {
        this.disruptor = new Disruptor<>(
                () -> new MessageBuffer(),
                bufferSize,
                new NamedThreadFactory(threadName),
                ProducerType.MULTI,
                new SleepingWaitStrategy()
        );

        disruptor.handleEventsWith(handler);
        disruptor.setDefaultExceptionHandler(new ErrorHandler<>());
    }

    public void start() {
        this.disruptor.start();
    }

    public void publish(BaseExecutor executor) {
        final long seq = getRingBuffer().next();
        final MessageBuffer buffer = getRingBuffer().get(seq);
        buffer.setExecutor(executor);
        this.disruptor.getRingBuffer().publish(seq);
    }

    public RingBuffer<MessageBuffer> getRingBuffer() {
        return this.disruptor.getRingBuffer();
    }

}
