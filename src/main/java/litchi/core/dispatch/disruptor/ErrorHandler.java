//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.dispatch.disruptor;

import com.lmax.disruptor.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 0x737263
 */
public class ErrorHandler<T> implements ExceptionHandler<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandler.class);

    @Override
    public void handleEventException(Throwable ex, long sequence, T event) {
        LOGGER.error("{}", ex);
        LOGGER.error("buffer = {}", event);
    }

    @Override
    public void handleOnStartException(Throwable ex) {
        LOGGER.error("{}", ex);
    }

    @Override
    public void handleOnShutdownException(Throwable ex) {
        LOGGER.error("{}", ex);
    }
}
