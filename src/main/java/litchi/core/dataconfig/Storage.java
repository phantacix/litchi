//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.dataconfig;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import litchi.core.common.utils.ReflectUtils;
import litchi.core.common.utils.StringUtils;
import litchi.core.dataconfig.annotation.FieldName;
import litchi.core.dataconfig.annotation.IndexPK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import litchi.core.common.utils.ReflectUtils;
import litchi.core.common.utils.StringUtils;
import litchi.core.dataconfig.annotation.FieldName;
import litchi.core.dataconfig.annotation.IndexPK;

/**
 * 配置实体存储类
 * @author 0x737263
 *
 */
public class Storage {
	private static final Logger LOGGER = LoggerFactory.getLogger(Storage.class);

	/** 文件名 */
	private String fileName;
	/** 存储类对应的配置类 */
	private Class<ConfigAdapter> clazz;

	/** 配置实体类存储列表 */
	private List<ConfigAdapter> modelAdapterList = new ArrayList<>();

	/** 非主键索引表 key:索引名,value:modelConfigMaps */
	private Map<String, List<ConfigAdapter>> indexTable = new HashMap<>();
	/** 主键索引表 key:索引值,valeu:modelConfigMaps */
	private Map<Object, ConfigAdapter> pkIndexTable = new HashMap<>();

	/** 带@Index的字段集合 key:indexName,value:Field */
	private Map<String, Field> fieldMaps = new HashMap<>();
	
	private Field indexPKField = null;

	public Storage(String fileName, Class<ConfigAdapter> clazz, List<ConfigAdapter> modelAdapterList) {
		this.fileName = fileName;
		this.clazz = clazz;
		this.modelAdapterList.addAll(modelAdapterList);
		buildIndex();
	}

	/**
	 * 为配置实体类列表建立索引表同时创建id索引表
	 */
	private void buildIndex() {
		if (this.modelAdapterList == null || this.modelAdapterList.isEmpty()) {
			return;
		}

		Field[] fields = this.clazz.getDeclaredFields();
		for (Field f : fields) {
			IndexPK indexPK = f.getAnnotation(IndexPK.class);
			FieldName filedName = f.getAnnotation(FieldName.class);

			if (indexPK != null) {
				f.setAccessible(true);
				this.indexPKField = f;
			}

			if (filedName != null && StringUtils.isNotBlank(filedName.indexName())) {
				f.setAccessible(true);
				this.fieldMaps.put(filedName.indexName(), f);
			}
		}

		createPKIndex(this.modelAdapterList);
		createIndex(this.modelAdapterList);
	}

	/**
	 * 处理主键索引
	 * @param modelAdapterList
	 */
	private void createPKIndex(List<ConfigAdapter> modelAdapterList) {
		if (this.indexPKField == null) {
			return;
		}

		for (ConfigAdapter modelAdapter : modelAdapterList) {
			Object indexValue = ReflectUtils.getFieldValue(this.indexPKField, modelAdapter);
			if (this.pkIndexTable.containsKey(indexValue)) {
				LOGGER.error("pk index value must be unique! class:{} field:{} index={}", modelAdapter.getClass(), this.indexPKField, indexValue);
				return;
			}

			// pk的索引单独存在一个集合.key:索引值,value:model对象
			this.pkIndexTable.put(indexValue, modelAdapter);
		}
	}

	private List<Object> getDistinctIndexValues(List<ConfigAdapter> list, Field field) {
		List<Object> valueList = new ArrayList<>();
		for (ConfigAdapter m : list) {
			Object indexValue = ReflectUtils.getFieldValue(field, m);
			if(!valueList.contains(indexValue)) {
				valueList.add(indexValue);	
			}
		}
		return valueList;
	}
	
	private List<ConfigAdapter> getModelAdapter(List<ConfigAdapter> list, Field filed, Object value) {
		List<ConfigAdapter> resultList = new ArrayList<>();
		for (ConfigAdapter m : list) {
			Object indexValue = ReflectUtils.getFieldValue(filed, m);
			try {
				
				if (indexValue.equals(value)) {
					resultList.add(m);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return resultList;
	}
	
	private LinkedHashMap<Object, List<ConfigAdapter>> getFilterList(String nextColumn, List<ConfigAdapter> list) {
		LinkedHashMap<Object, List<ConfigAdapter>> valueMaps = new LinkedHashMap<>();
		Field f = this.fieldMaps.get(nextColumn);
		if (f == null) {
			LOGGER.error("column not exist! class:[{}] column name:[{}]", this.clazz.getSimpleName(), nextColumn);
			return valueMaps;
		}
		List<Object> valueList = getDistinctIndexValues(list, f);
		for (Object value : valueList) {
			List<ConfigAdapter> tmpList = getModelAdapter(list, f, value);
			if (!tmpList.isEmpty()) {
				valueMaps.put(value, tmpList);
			}
		}
		return valueMaps;
	}
	
	private boolean filter(List<ConfigAdapter> modelList, List<String> columnsList, String newIndexName) {
		if (columnsList.isEmpty() || modelList.isEmpty()) {
			return false;
		}

		String column = columnsList.remove(0);
		LinkedHashMap<Object, List<ConfigAdapter>> resultMaps = getFilterList(column, modelList);
		for (Entry<Object, List<ConfigAdapter>> entry : resultMaps.entrySet()) {
			if (entry.getValue().isEmpty()) {
				continue;
			}

			String newKey = getIndexKey(newIndexName, entry.getKey());
			this.indexTable.put(newKey, entry.getValue());

			if (!filter(entry.getValue(), new ArrayList<>(columnsList), newKey)) {
				continue;
			}
		}
		return true;
	}

	/**
	 * 根据配置实体类的索引字段和对应索引值为索引表建立索引
	 * @param modelAdapterList
	 */
	private void createIndex(List<ConfigAdapter> modelAdapterList) {
		if (this.fieldMaps.isEmpty()) {
			return;
		}

		if (modelAdapterList.isEmpty()) {
			return;
		}

		List<IndexObject> indexObjectList = new ArrayList<>();
		modelAdapterList.get(0).registerIndex(indexObjectList);

		for (IndexObject indexObject : indexObjectList) {
			if (indexObject.getColumnList().isEmpty()) {
				LOGGER.error("IndexObject column is empty. object{}", indexObject);
				continue;
			}

			List<String> columnsList = new ArrayList<>();
			columnsList.addAll(indexObject.getColumnList());

			LinkedHashMap<Object, List<ConfigAdapter>> valueMaps = getFilterList(columnsList.remove(0), modelAdapterList);
			for (Entry<Object, List<ConfigAdapter>> entry : valueMaps.entrySet()) {
				List<String> newColumnsList = new ArrayList<>(columnsList);
				String newIndexName = getIndexKey(indexObject.getIndexName(), entry.getKey());
				this.indexTable.put(newIndexName, entry.getValue());
				filter(entry.getValue(), newColumnsList, newIndexName);
			}
		}
	}

	/**
	 * 根据实体类中索引字段和该字段对应的索引值 为indexTable创建Indexkey
	 * @param indexName  索引字段名字
	 * @param columnValue 索引字段值
	 * @return
	 */
	private String getIndexKey(String indexName, Object... columnValue) {
		StringBuilder key = new StringBuilder();
		key.append(indexName.toLowerCase());
		for (Object value : columnValue) {
			key.append("_").append(value.toString().toLowerCase());
		}
		return key.toString();
	}

	public List<ConfigAdapter> getListAll() {
		return modelAdapterList;
	}

	/**
	 * 根据指定的配置类索引属性（索引字段和索引值）获取相应配置类列表
	 * @param indexName 索引名字
	 * @param indexValue 索引值
	 * @return
	 */
	public List<ConfigAdapter> getList(String indexName, Object... indexValue) {
		String indexKey = getIndexKey(indexName, indexValue);
		if (StringUtils.isBlank(indexKey)) {
			return Collections.emptyList();
		}

		List<ConfigAdapter> modelAdapters = this.indexTable.get(indexKey);
		if (modelAdapters == null) {
			return Collections.emptyList();
		}

		return modelAdapters;
	}

	/**
	 * 通过id值获取配置类
	 * @param id
	 * @return
	 */
	public ConfigAdapter getModel(Object id) {
		if (id == null) {
			return null;
		}
		return this.pkIndexTable.get(id);
	}

	public String getFileName() {
		return fileName;
	}
	
	public Class<ConfigAdapter> getAdapterClazz() {
		return clazz;
	}

	@Override
	public String toString() {
		boolean hasPk = this.indexPKField != null;
		return String.format("[class:%s, hasPK:%s, indexSize:%s, modelSize:%s]", this.clazz.getSimpleName(), hasPk, indexTable.size(),
				this.modelAdapterList.size());
	}

}
