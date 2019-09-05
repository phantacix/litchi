//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.common.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程工厂封装扩展类
 * 
 * @author 0x737263
 * 
 */
public class NamedThreadFactory implements ThreadFactory {
	final ThreadGroup group;
	final AtomicInteger threadNumber = new AtomicInteger(1);
	final String namePrefix;

	public NamedThreadFactory(String name) {
		this.group = new ThreadGroup(name);
		this.namePrefix = (group.getName() + "-");
	}

	public Thread newThread(Runnable r) {
		return new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
	}

	public Thread newThread(Runnable r, String title) {
		return new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement() + title, 0L);
	}
}