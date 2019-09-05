//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.jdbc.sql;

/**
 * 插入sql语句实现
 */
public class InsertStatement extends AbstractStatement {

	public InsertStatement() {
		super();
	}

//	/**
//	 * 获取占位符(?,?,)
//	 * @return
//	 */
//	private String getPlaceHolderWithComma(String[] allDbColumName) {
//		StringBuffer sql = new StringBuffer();
//		String[] fields = allDbColumName;
//		for (int i = 0; i < fields.length; i++) {
//			sql.append(PLACEHOLDER);
//			if (i != (fields.length - 1)) {
//				sql.append(COMMA);
//			}
//		}
//		return sql.toString();
//	}
}
