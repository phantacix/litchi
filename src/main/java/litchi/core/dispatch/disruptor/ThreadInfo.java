//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.dispatch.disruptor;

public class ThreadInfo {
	
	public String name;

	public int threadId;

	public int threadNum;

	public static ThreadInfo valueOf(String name, int threadId, int threadNum) {
		ThreadInfo info = new ThreadInfo();
		info.name = name;
		info.threadId = threadId;
		info.threadNum = threadNum;
		return info;
	}
}
