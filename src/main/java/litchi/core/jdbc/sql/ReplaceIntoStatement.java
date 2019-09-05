//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.jdbc.sql;

/**
 * Replace into sql语句实现
 * @author 
 *
 */
public class ReplaceIntoStatement extends AbstractStatement {


	public ReplaceIntoStatement() {
		super();
	}

	/**
	 * 获取占位符(?,?,)
	 * @return
	 */
	private String getPlaceHolderWithComma(String[] allDbColumnName) {
		StringBuilder sql = new StringBuilder();
		String[] fields = allDbColumnName;
		for (int i = 0; i < fields.length; i++) {
			sql.append(PLACEHOLDER);
			if (i != (fields.length - 1)) {
				sql.append(COMMA);
			}
		}
		return sql.toString();
	}

	public String toSqlString(String tableName, String[] columnName) {
		StringBuilder sql = new StringBuilder();
		sql.append(REPLACE_INTO);
		sql.append(SINGLE_QUOTE + tableName + SINGLE_QUOTE);
		sql.append(PARENTHESES_LEFT);
		sql.append(getColumStrWithComma(columnName));
		sql.append(PARENTHESES_RIGHT);
		sql.append(VALUES);
		sql.append(PARENTHESES_LEFT);
		sql.append(getPlaceHolderWithComma(columnName));
		sql.append(PARENTHESES_RIGHT);
		return sql.toString();
	}

}
