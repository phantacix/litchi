//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.jdbc.sql;

/**
 * 查询sql语句实现
 * @author 
 *
 */
public class SelectStatement extends AbstractStatement {

	public SelectStatement() {
		super();
	}

	public String toSqlString(String pkName, String tableName, String[] allDbColumName) {
		return toSqlString(pkName, tableName, allDbColumName, null);
	}

	public String toSqlString(String pkName, String tableName, String[] allDbColumName, String[] keys) {
		return toSqlString(pkName, tableName, allDbColumName, null, -1, -1, keys);
	}
	
	public String toSqlString(String pkName, String tableName, String[] allDbColumName, String[] keys, String otherCondition) {
		return toSqlString(pkName, tableName, allDbColumName, null, -1, -1, keys, otherCondition);
	}

	public String toSqlString(String pkName, String tableName, String[] allDbColumName, String columnName, String[] keys) {
		return toSqlString(pkName, tableName, allDbColumName, columnName, -1, -1, keys);
	}
	
	public String toSqlString(String pkName, String tableName, String[] allDbColumName, String columnName, String[] keys, String otherCondition) {
		return toSqlString(pkName, tableName, allDbColumName, columnName, -1, -1, keys, otherCondition);
	}

	public String toSqlString(String pkName, String tableName, String[] allDbColumName, String columnName, int limitBegin, int limitEnd, String[] keys) {
		return toSqlString(pkName, tableName, allDbColumName, columnName, limitBegin, limitEnd, keys, null);
	}
	
	public String toSqlString(String pkName, String tableName, String[] allDbColumName, String columnName, int limitBegin, int limitEnd, String orderByColumn, String orderByType, String[] keys, String otherCondition) {
		String columnString = null;
		if (null != columnName) {
			columnString = columnName;
		} else {
			columnString = getColumStrWithComma(allDbColumName);
		}

		StringBuffer sql = new StringBuffer();
		sql.append(SELECT);
		sql.append(columnString);
		sql.append(FROM);
		sql.append(SINGLE_QUOTE + tableName + SINGLE_QUOTE);
		if (keys != null && keys.length > 0) {
			sql.append(WHERE);
			sql.append(getConditionWithPlaceHolder(keys));
			if (otherCondition != null && otherCondition.length() > 0) {
				sql.append(AND);
				sql.append(otherCondition);
			}
		} else if (otherCondition != null && otherCondition.length() > 0) {
			sql.append(WHERE);
			sql.append(otherCondition);
		}
		
		if (null != orderByColumn) {
			sql.append(ORDER_BY).append(orderByColumn);
		}
		if (null != orderByType) {
			sql.append(SPACE).append(orderByType);
		}

		if (limitBegin > 0 && limitEnd > 0) {
			sql.append(LIMIT);
			sql.append(PLACEHOLDER);
			sql.append(COMMA);
			sql.append(PLACEHOLDER);
		} else if (limitBegin == 0 && limitEnd > 0) {
			sql.append(LIMIT);
			sql.append(PLACEHOLDER);
		}
		return sql.toString();
	}

	public String toSqlString(String pkName, String tableName, String[] allDbColumName, String columnName, int limitBegin, int limitEnd, String[] keys, String otherCondition) {
		return toSqlString(pkName, tableName, allDbColumName, columnName, limitBegin, limitEnd, null, null, keys, otherCondition);
	}

	public String toSqlString(String pkName, String tableName, String[] allDbColumName, int limitBegin, int limitEnd, String[] keys) {
		return toSqlString(pkName, tableName, allDbColumName, null, limitBegin, limitEnd, keys);
	}
	
	public String toSqlString(String pkName, String tableName, String[] allDbColumName, int limitBegin, int limitEnd, String orderByColumn, String orderByType, String[] keys) {
		return toSqlString(pkName, tableName, allDbColumName, null, limitBegin, limitEnd, orderByColumn, orderByType, keys, null);
	}

}
