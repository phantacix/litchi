//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.common.utils;

/**
 * @author Phil
 * Date:   2018/4/8
 */
public class ServerTime {
	private static long timeOffset = 0;

	public static long timeSecond() {
		return (System.currentTimeMillis() + timeOffset) / 1000;
	}
	
    public static long timeMillis() {
        return System.currentTimeMillis() + timeOffset;
    }

	public static void offset(long offset) {
		timeOffset = offset;
	}
}
