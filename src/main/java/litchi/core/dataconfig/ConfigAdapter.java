//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.dataconfig;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * config包中的类继承于此
 * @author 0x737263
 */
public interface ConfigAdapter {
	Logger LOGGER = LoggerFactory.getLogger(ConfigAdapter.class);

	/**
	 * 初始化处理方法(用于model初始化时做一些自定义处理)
	 */
	void initialize();

	/**
	 * 索引注册
	 * @return
	 */
	void registerIndex(List<IndexObject> index);

}
