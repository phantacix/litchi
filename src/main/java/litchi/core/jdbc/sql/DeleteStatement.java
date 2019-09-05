//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.jdbc.sql;

/**
 * 删除sql语句实现
 */
public class DeleteStatement extends AbstractStatement {

	public DeleteStatement() {
		super();
	}

	public String toSqlString(String pkName, String tableName, String[] key) {
		StringBuffer sql = new StringBuffer();
		sql.append(DELETE);
		sql.append(FROM);
		sql.append(SINGLE_QUOTE + tableName + SINGLE_QUOTE);
		if (key == null || key.length <= 0) {
			key = new String[] { pkName };
		}
		sql.append(WHERE);
		sql.append(getConditionWithPlaceHolder(key));

		return sql.toString();
	}

}
