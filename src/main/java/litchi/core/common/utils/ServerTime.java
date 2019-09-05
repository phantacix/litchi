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
	private static long offset = 0;

	public static long timeSecond() {
		return (System.currentTimeMillis() + offset) / 1000;
	}
	
    public static long timeMillis() {
        return System.currentTimeMillis() + offset;
    }

	public static void offset(long offset) {
		ServerTime.offset = offset;
	}
}
