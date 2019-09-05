//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.jdbc.sql;

/**
 *
 */
public abstract class AbstractStatement {
	protected static final String SPACE = " ";
	protected static final String PLACEHOLDER = " ? ";
	protected static final String AND = " AND ";
	protected static final String COMMA = " ,";
	protected static final String EQUATE = " = ";
	protected static final String SELECT = "SELECT ";
	protected static final String FROM = " FROM ";
	protected static final String WHERE = " WHERE ";
	protected static final String LIMIT = " LIMIT ";
	protected static final String ORDER_BY = " ORDER BY ";
	protected static final String DELETE = "DELETE ";
	protected static final String UPDATE = "UPDATE ";
	protected static final String SET = " SET ";
	protected static final String INSERT_INTO = "INSERT INTO ";
	protected static final String VALUES = " VALUES ";
	protected static final String REPLACE_INTO = "REPLACE INTO ";
	protected static final String SINGLE_QUOTE = "`";
	protected static final String PARENTHESES_LEFT = " ( ";
	protected static final String PARENTHESES_RIGHT = " ) ";

	public AbstractStatement() {
	}

	/**
	 * 获取列名(xxx, xxx,)
	 * @return
	 */
	protected String getColumStrWithComma(String[] allDbColumName) {
		StringBuffer columString = new StringBuffer();
		for (int i = 0; i < allDbColumName.length; i++) {
			String f = allDbColumName[i];
			columString.append(SINGLE_QUOTE + f + SINGLE_QUOTE);
			if (i != (allDbColumName.length - 1)) {
				columString.append(COMMA);
			}
		}
		return columString.toString();
	}

	/**
	 * 获取条件语句（xxx = ?)
	 * @return
	 */
	protected String getConditionWithPlaceHolder(String[] key) {
		StringBuffer sql = new StringBuffer();
		for (int i = 0; i < key.length; i++) {
			String c = key[i];
			sql.append(c);
			sql.append(EQUATE);
			sql.append(PLACEHOLDER);
			if (i != (key.length - 1)) {
				sql.append(AND);
			}
		}
		return sql.toString();
	}

}
