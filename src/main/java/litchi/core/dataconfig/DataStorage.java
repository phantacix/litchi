//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.dataconfig;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 配置数据缓存管理
 * @author Paopao
 * 		   2018-08-01
 */
public class DataStorage {
	
	protected static final Logger LOGGER = LoggerFactory.getLogger(DataStorage.class);

	/**
	 * model类与相应存储类的映射    key:model类， value:Storage存储类
	 */
	protected ConcurrentHashMap<Class<ConfigAdapter>, Storage> modelStorageMaps = new ConcurrentHashMap<>();
	
	/**
	 * model类与名称的映射. key:DataFile.fileName() value:Class
	 */
	protected ConcurrentHashMap<String, Class<ConfigAdapter>> modelConfigMaps = new ConcurrentHashMap<>();

	public <T extends ConfigAdapter> T getFirst(Class<T> modelClass, IndexObject indexObject, Object... params) {
		List<T> list = getList(modelClass, indexObject, params);
		if (list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends ConfigAdapter> List<T> getList(Class<T> modelClazz) {
		if (modelClazz == null) {
			LOGGER.warn("modelClass param is null.");
			return Collections.emptyList();
		}

		Storage storage = modelStorageMaps.get(modelClazz);
		if (storage == null) {
			LOGGER.warn("call data data file: [{}] is null!", modelClazz.getSimpleName());
			return Collections.emptyList();
		}

		List<ConfigAdapter> list = storage.getListAll();
		if (list == null) {
			LOGGER.warn("call data data file: [{}] is null!", modelClazz.getSimpleName());
			return Collections.emptyList();
		}

		return (List<T>) list;
	}

	@SuppressWarnings("unchecked")
    public <T extends ConfigAdapter> List<T> getList(Class<T> modelClass, IndexObject indexObject, Object... params) {
		if (modelClass == null || indexObject == null || params.length < 1) {
			return Collections.emptyList();
		}

		Storage storage = modelStorageMaps.get(modelClass);
		if (storage == null) {
			return Collections.emptyList();
		}

		return (List<T>) storage.getList(indexObject.getIndexName(), params);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends ConfigAdapter> T getModel(Class<T> modelClass, Object id) {
		if (modelClass == null || id == null) {
			return null;
		}

		Storage storage = modelStorageMaps.get(modelClass);
		if (storage == null) {
			return null;
		}

		return (T) storage.getModel(id);
	}

	public void addStorage(Storage storage) {
		modelStorageMaps.put(storage.getAdapterClazz(), storage);
		modelConfigMaps.put(storage.getFileName(), storage.getAdapterClazz());
	}

	public Class<ConfigAdapter> getClassByFileName(String fileName) {
		return modelConfigMaps.get(fileName);
	}
}
