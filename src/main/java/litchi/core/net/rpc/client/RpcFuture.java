//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.net.rpc.client;

import litchi.core.net.rpc.RpcConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import litchi.core.net.rpc.RpcConfig;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RpcFuture<R> implements Future<R> {
	private static final Logger LOGGER = LoggerFactory.getLogger(RpcFuture.class);

	private volatile R response;
	private volatile Exception exception;
	private volatile boolean done;
	private volatile int waiters;
	
	private boolean await(long timeout, TimeUnit unit) {
		long timeoutMillis = unit.toMillis(timeout);
		long endTime = System.currentTimeMillis() + timeoutMillis;
		synchronized (this) {
			if (done)
				return done;
			if (timeoutMillis <= 0)
				return done;
			waiters++;
			try {
				while (!done) {
					wait(timeoutMillis);
					if (endTime < System.currentTimeMillis() && !done) {
						exception = new TimeoutException("time out");
						break;
					}
				}
			}	
			catch(Exception ex) {
				this.exception = ex;
			} finally {
				waiters--;
			}
		}
		return done;
	}

	public Exception getException() {
		return exception;
	}

	public R getResponse() {
		Exception e = getException();
		if (e != null) {
			LOGGER.error("{}", e);
		}
		return response;
	}

	public void setResponse(R response) {
		synchronized (this) {
			if (done)
				return;
			this.response = response;
			done = true;
			if (waiters > 0) {
				notifyAll();
			}
		}
	}

	@Override
	public R get() {
		return get(RpcConfig.RPC_TIMEOUT, TimeUnit.MILLISECONDS);
	}

	@Override
	public R get(long timeout, TimeUnit unit) {
		await(timeout, unit);
		return getResponse();
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		return done;
	}

}
