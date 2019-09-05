//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.common.extend;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 毫秒级计时器
 * @author 0x737263
 *
 */
public class StopWatch {
    static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private long startTime;
	private long endTime;
	
	public StopWatch(boolean isStart) {
		if (isStart) {
			start();
		}
	}
	
	public void start() {
		this.startTime = System.currentTimeMillis();
	}

	public void stop() {
		this.endTime = System.currentTimeMillis();
	}
	
	public long runTime() {
		return this.endTime - this.startTime;
	}

	public long getStartTime() {
		return startTime;
	}

    public String formatStartTime() {
        return dateformat.format(new Date(startTime));
    }

	public long getEndTime() {
		return endTime;
	}

    public String formatEndTime() {
        return dateformat.format(new Date(endTime));
    }

}
