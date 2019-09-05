//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.jdbc.table;

import litchi.core.common.utils.PathResolver;
import litchi.core.jdbc.annotation.TableName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import litchi.core.common.utils.PathResolver;
import litchi.core.jdbc.annotation.TableName;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public abstract class SuperTable {
	private static final Logger LOGGER = LoggerFactory.getLogger(SuperTable.class);

	protected static Map<Class<? extends Table>, TableInfo> TABLE_INFO_MAPS = new HashMap<>();
	
	/**
	 * 
	 * @param packageScanArray
	 */
	public static void tableScan(String... packageScanArray) {
		Collection<Class<? extends Table>> collection = PathResolver.scanPkgWithFather(Table.class, packageScanArray);
		try {

			for (Class<? extends Table> clz : collection) {
				if (clz == null) {
					continue;
				}

				TableName tableName = clz.getAnnotation(TableName.class);
				if (tableName == null) {
					continue;
				}
				TABLE_INFO_MAPS.put(clz, TableInfo.valueOf(clz, tableName));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		TABLE_INFO_MAPS.values().forEach(tableInfo -> LOGGER.info("Table:{}, Columns:{}", tableInfo.annotation().tableName(), tableInfo.buildDbColumns()));
	}

	public <T extends Table<?>> TableInfo getTableInfo() {
		return TABLE_INFO_MAPS.get(this.getClass());
	}

    public String tableName() {
        return getTableInfo().annotation().tableName();
    }

	public static TableInfo getTableInfo(Class<?> clazz) {
		return TABLE_INFO_MAPS.get(clazz);
	}
	
	public static Collection<TableInfo> getTableInfoList() {
		return TABLE_INFO_MAPS.values();
	}
}
