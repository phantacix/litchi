//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.jdbc.table;

import com.esotericsoftware.reflectasm.FieldAccess;
import litchi.core.jdbc.column.AbstractColumnParser;
import litchi.core.jdbc.column.ColumnContext;
import org.apache.commons.dbutils.ResultSetHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import litchi.core.jdbc.column.AbstractColumnParser;
import litchi.core.jdbc.column.ColumnContext;
import litchi.core.jdbc.table.TableInfo.TableColumnInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 所有实体继承于此,实现已下方法
 * @author 0x737263
 *
 * @param <PK>主键类型
 */
public abstract class Table<PK> extends SuperTable implements ResultSetHandler<Table<PK>> { // RowMapper<Table<PK>>
	private static final Logger LOGGER = LoggerFactory.getLogger(Table.class);

	/**
	 * 获取主键值
	 * @return
	 */
	public abstract PK getPkId();

	/**
	 * 设置主键值
	 * @param pk
	 */
	public abstract void setPkId(PK pk);

	/**
	 * 读取完表数据后如需处理额外逻辑，请重写此方法
	 */
	protected void readComplete() {
	}

	protected void writeBefore() {
	}


	@Override
	public Table<PK> handle(ResultSet rs) throws SQLException {
		TableInfo tableInfo = getTableInfo(this.getClass());
		Table<?> entity = tableInfo.classAccess.newInstance();
		entity.readData(tableInfo, rs);
		// 读取完表数据后处理额外逻辑
		entity.readComplete();
		return (Table<PK>) entity;
	}

//	@SuppressWarnings("unchecked")
//	@Override
//	public Table<PK> mapRow(ResultSet rs, int rowNum) throws SQLException {
//		TableInfo tableInfo = getTableInfo(this.getClass());
//		Table<?> entity = tableInfo.classAccess.newInstance();
//		entity.readData(tableInfo, rs);
//		// 读取完表数据后处理额外逻辑
//		entity.readComplete();
//		return (Table<PK>) entity;
//	}

	/**
	 * 从db读取每一行记录,续承类可以重写自定义读取方式
	 * @param tableInfo
	 * @param rs
	 */
	public void readData(TableInfo tableInfo, ResultSet rs) throws SQLException {

		for (TableColumnInfo columnInfo : tableInfo.columnInfoList) {
			Class<?> columnType = columnInfo.getColumnType(0);

			AbstractColumnParser parser = ColumnContext.getParser(columnType);
			if (parser == null) {
				LOGGER.error("Read data error.table {} column type {} not implemented.", tableInfo.annotation().tableName(), columnInfo);
				continue;
			}
			try {
				parser.readColumn(this, columnInfo, rs);
			} catch (Exception ex) {
				LOGGER.error("read game error! className={} fieldName={}", tableInfo.clazz().getName(), columnInfo.fieldName);
				LOGGER.error("", ex);
			}
		}
	}

	/**
	 * 获取所有字段的值,,续承类可以重写自定义读取方式
	 * @return
	 */
	public Object[] writeData() {
		TableInfo tableInfo = getTableInfo(getClass());
		FieldAccess fieldAccess = tableInfo.fieldAccess;

		ArrayList<Object> writeList = new ArrayList<>();
		for (TableColumnInfo columnInfo : tableInfo.columnInfoList) {
			Class<?> columnType = columnInfo.getColumnType(0);
			AbstractColumnParser parser = ColumnContext.getParser(columnType);
			if (parser == null) {
				LOGGER.error("Read data error.column type {} not implemented.", columnInfo);
				continue;
			}
			writeBefore();
			Object fieldValue = null;
			try {
				fieldValue = fieldAccess.get(this, columnInfo.fieldName);
				parser.writeColumn(writeList, columnInfo, fieldValue);
			} catch (Exception ex) {
				LOGGER.error("write game error! className={} fieldName={} value={}", tableInfo.clazz().getName(), columnInfo.fieldName, fieldValue);
				LOGGER.error("{}", ex);
			}
		}

		return writeList.toArray();
	}
}
