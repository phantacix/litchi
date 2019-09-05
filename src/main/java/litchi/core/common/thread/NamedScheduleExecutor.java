//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.common.thread;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * 线程命名的执行器封装类
 * @author PhilShen
 *
 */
public class NamedScheduleExecutor extends ScheduledThreadPoolExecutor {

	public NamedScheduleExecutor(int poolSize, final String name) {
		super(poolSize, new NamedThreadFactory(name));
	}

}
