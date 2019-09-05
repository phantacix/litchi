//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.dataconfig;

import java.util.List;

import litchi.core.components.Component;
import litchi.core.components.Component;

/**
 * 数据配置接口
 * @author 0x737263
 */
public interface DataConfig extends Component {
	
	/**
	 * 获取数据源
	 * @return
	 */
	DataConfigSource getDataSource();

	/**
	 * 根据类名获取数据配置列表
	 * @param modelClass
	 * @param indexObject
	 * @param params
	 * @param <T>
	 * @return
	 */
	<T extends ConfigAdapter> T getFirst(Class<T> modelClass, IndexObject indexObject, Object... params);

	/**
	 * 根据类名获取数据配置列表
	 * @param modelClass	需要获取的Model类
	 * @return
	 */
	<T extends ConfigAdapter> List<T> getList(Class<T> modelClass);

	/**
	 * 根据指定的配置类索引属性（索引字段和索引值）获取相应配置类列表
	 * @param modelClass	配置类
	 * @param indexObject	索引对象
	 * @param params		参数
	 * @param <T>
	 * @return
	 */
	<T extends ConfigAdapter> List<T> getList(Class<T> modelClass, IndexObject indexObject, Object... params);
	
	/**
	 * 根据指定的配置类的id值获取相应配置
	 * @param modelClass
	 * @param id
	 * @return
	 */
	<T extends ConfigAdapter> T getModel(Class<T> modelClass, Object id);

	/**
	 * 保存byte[] data至newconfig文件夹
	 * @param fileName
	 * @param text
	 * @return
	 */
	boolean flush2NewConfig(String fileName, String text);
	
	/**
	 * 根据文件名获取映射类
	 * @param fileName
	 * @return
	 */
	Class<ConfigAdapter> getClassByFileName(String fileName);

	/**
	 * 重新加载配置
	 * @param fileName
	 * @param text
	 */
	void reloadConfig(String fileName, String text);

	/**
	 * 检则配置
	 * @param fileName
	 * @param text
	 * @return
	 */
	boolean checkModelAdapter(String fileName, String text);
	
	/**
	 * 注册配置管理器
	 * @param configService
	 */
	void registerService(ConfigService configService);

}
