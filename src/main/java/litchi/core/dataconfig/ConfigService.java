//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.dataconfig;

/**
 * 配置管理
 * @author Phil
 * 		   2018-04-17
 */
public interface ConfigService {

	/**
	 * 清理配置对象
	 *
	 * @param clazz 已经更新的配置对象
	 */
	void clean(Class<ConfigAdapter> clazz);

	/**
	 * 初始化时
	 * @param dataConfig
	 */
	void initialize(DataConfig dataConfig);

}
