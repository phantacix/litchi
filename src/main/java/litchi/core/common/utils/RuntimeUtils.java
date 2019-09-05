//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.common.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import litchi.core.common.utils.StringUtils;

public class RuntimeUtils {
	
	public static Long pid() {
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
		String name = runtimeMXBean.getName();
		return Long.valueOf(StringUtils.split(name, "@")[0]);
	}
}
