//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.dispatch.disruptor;

import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;

public class ThreadInfo {

    public String name;

    public int threadId;

    public int threadNum;

    public WaitStrategy waitStrategy;

    public static ThreadInfo valueOf(String name, int threadId, int threadNum) {
        return valueOf(name, threadId, threadNum, new SleepingWaitStrategy());
    }

    public static ThreadInfo valueOf(String name, int threadId, int threadNum, WaitStrategy waitStrategy) {
        ThreadInfo info = new ThreadInfo();
        info.name = name;
        info.threadId = threadId;
        info.threadNum = threadNum;
        info.waitStrategy = waitStrategy;
        return info;
    }

}
