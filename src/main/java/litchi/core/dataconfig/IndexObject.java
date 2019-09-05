//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.dataconfig;

import java.util.Arrays;
import java.util.List;

/**
 * 索引对象
 * @author 0x737263
 *
 */
public class IndexObject {
	/** 索引名 */
	private String indexName;
	/** 索引字段列表 */
	private List<String> columnsList;

	public String getIndexName() {
		return indexName;
	}

	public List<String> getColumnList() {
		return columnsList;
	}

	public static IndexObject valueOf(String key1) {
		return build(key1);
	}

	public static IndexObject valueOf(String key1, String key2) {
		return build(key1, key2);
	}

	public static IndexObject valueOf(String key1, String key2, String key3) {
		return build(key1, key2, key3);
	}

	/**
	 * 建立多字段索引原则
	 * 0.为了保证加载性能，目前仅开放最多3个字段的组合索引;
	 * 1.创建索引逻辑是按照多个字段的先后顺序来排列组合创建索引的;
	 * 2.例如有a,b,c 三个字段，通过该IndexObject可以查询  a|a,b|a,b,c 三种索引。注意(a,c|b,c 是不能查的！)。
	 * @param indexKey
	 * @return
	 */
	public static IndexObject build(String... indexKey) {
		IndexObject obj = new IndexObject();

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < indexKey.length; i++) {
			if (i != 0) {
				sb.append("_");
			}
			sb.append(indexKey[i]);
		}

		obj.indexName = sb.toString();
		obj.columnsList = Arrays.asList(indexKey);

		return obj;
	}

	@Override
	public String toString() {
		return String.format("indexName:%s, columns:%s", indexName, columnsList);
	}
}
