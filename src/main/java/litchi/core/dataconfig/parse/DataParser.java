//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.dataconfig.parse;

import litchi.core.dataconfig.ConfigAdapter;

import java.util.List;

/**
 * 数据解析接口
 * 
 * @author 0x737263
 * 
 */
public interface DataParser {

	/**
	 * 读取配置文件后进行解析
	 *
	 * @param text  文件内容
	 * @param className 解析映射类文件
	 * @return
	 */
	<T extends ConfigAdapter> List<T> parse(String text, Class<T> className);

	/**
	 * 文件扩展名
	 *
	 * @return
	 */
	String fileExtensionName();
}
