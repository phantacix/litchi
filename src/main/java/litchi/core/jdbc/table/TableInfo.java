//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.jdbc.table;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import litchi.core.common.utils.ReflectUtils;
import litchi.core.common.utils.StringUtils;
import litchi.core.jdbc.annotation.Column;
import litchi.core.jdbc.annotation.TableName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.reflectasm.ConstructorAccess;
import com.esotericsoftware.reflectasm.FieldAccess;

import litchi.core.common.utils.ReflectUtils;
import litchi.core.common.utils.StringUtils;
import litchi.core.jdbc.annotation.Column;
import litchi.core.jdbc.annotation.TableName;

/**
 * 实体信息类,用于记录反射后的一些常用信息
 * @author 0x737263
 *
 */
@SuppressWarnings("rawtypes")
public class TableInfo {
	private static final Logger LOGGER = LoggerFactory.getLogger(TableInfo.class);

	private Class<? extends Table> clazz;

	private TableName tableName;

	private String pkName;

	public List<TableColumnInfo> columnInfoList = new ArrayList<>();

	public ConstructorAccess<? extends Table> classAccess;

	public FieldAccess fieldAccess;

	public Table<?> newInstance() {
		return classAccess.newInstance();
	}

	public TableName annotation() {
	    return tableName;
    }

    public String pkName() {
        return pkName;
    }

    public Class<? extends Table> clazz() {
        return clazz;
    }

    public static TableInfo valueOf(Class<? extends Table> clazz, TableName tableName) {
        TableInfo tableInfo = new TableInfo();
        tableInfo.clazz = clazz;
        tableInfo.tableName = tableName;

        tableInfo.classAccess = ConstructorAccess.get(clazz);
        tableInfo.fieldAccess = FieldAccess.get(clazz);

        //reflect column
        tableInfo.reflectColumn(clazz.getDeclaredFields());

        if (StringUtils.isBlank(tableInfo.pkName)) {
            LOGGER.error(tableName.tableName() + "实体缺少主键");
        }
        return tableInfo;
    }

	private void reflectColumn(Field[] fields) {
		for (Field field : fields) {
			Column column = field.getAnnotation(Column.class);
			if (column == null) {
				continue;
			}

//			if (!Modifier.isPublic(field.getModifiers())) {
//				throw new RuntimeException(this.clazz.getName() + " 被@Column标注的Field必需为public.");
//			}

			String pkName = field.getName();
			String pkAliasName = column.alias();

			if (column.pk()) {
				if (StringUtils.isNotBlank(this.pkName)) {
					throw new RuntimeException(this.clazz.getName()  + " 禁止设置多个主键.");
				}
				this.pkName = StringUtils.isNotBlank(pkAliasName) ? pkAliasName : pkName;
			}

			List<Class<?>> fileTypeList = ReflectUtils.reflectFieldType(field);
			String aliasName = StringUtils.isBlank(pkAliasName) ? pkName : pkAliasName;
			columnInfoList.add(new TableColumnInfo(pkName, aliasName, fileTypeList));
		}
	}
	
	public String[] buildDbColumns() {
		String[] columns = new String[columnInfoList.size()];
		for (int i = 0; i < columnInfoList.size(); i++) {
			columns[i] = columnInfoList.get(i).aliasName;
		}
		return columns;
	}
	
	@Override
	public String toString() {
		LOGGER.info("-----------" + this.tableName + "-------------------");
		for (TableColumnInfo columnInfo : columnInfoList) {
			LOGGER.info(columnInfo.toString());
		}
		LOGGER.info("-----------" + this.tableName + "-------------------");
		return super.toString();
	}
	
	/**
	 * 
	 * @author 0x737263
	 *
	 */
	public class TableColumnInfo {
		public String fieldName;
		public String aliasName;
		public List<Class<?>> columnTypeList;
		
		public TableColumnInfo(String fieldName, String aliasName, List<Class<?>> columnTypeList) {
			this.fieldName = fieldName;
			this.aliasName = aliasName;
			this.columnTypeList = columnTypeList;
		}

		@Override
		public String toString() {
			return "TableColumnInfo [columnName=" + fieldName + ", aliasName=" + aliasName + ", columnTypeList=" + columnTypeList + "]";
		}

		public Class<?> getColumnType(int index) {
			return columnTypeList.get(index);
		}

	}
}
