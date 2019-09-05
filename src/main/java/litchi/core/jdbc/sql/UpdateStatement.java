//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.jdbc.sql;

import java.util.ArrayList;
import java.util.List;

/**
 * 更新语句实现
 * @author 
 *
 */
public class UpdateStatement extends AbstractStatement {

	public UpdateStatement() {
		super();
	}

	/**
	 * 去除条件列明
	 * @return
	 */
	private String[] exceptContition(String pkName, String[] allDbColumName, String[] condition) {
		if (condition == null || condition.length == 0) {
			condition = new String[] { pkName };
		}

		String[] fields = allDbColumName;
		List<String> arr = new ArrayList<>();
		for (String field : fields) {
			for (String c : condition) {
				if (!field.equals(c)) {
					arr.add(field);
				}
			}
		}
		String[] result = new String[arr.size()];
		return arr.toArray(result);
	}

	/**
	 * 获取列明（xxx = ?)
	 * @return
	 */
	private String getColumWithPlaceHolder(String pkName, String[] allDbColumName, String[] condition) {
		StringBuffer sql = new StringBuffer();
		String[] fields = exceptContition(pkName, allDbColumName, condition);

		return getConditionWithPlaceHolder(fields);
	}

	/**
	 * 获取条件(xxx = ?)
	 * @return
	 */
	private String getConditonWithPlaceHolder(String pkName, String[] allDbColumName, String[] condition) {
		if (condition == null || condition.length == 0) {
			condition = new String[] { pkName };
		}

		return getConditionWithPlaceHolder(condition);
	}

	public String toSqlString(String pkName, String tableName, String[] allDbColumName, String... conditon) {
		StringBuffer sql = new StringBuffer();
		sql.append(UPDATE);
		sql.append(SINGLE_QUOTE + tableName + SINGLE_QUOTE);
		sql.append(SET);
		sql.append(getColumWithPlaceHolder(pkName, allDbColumName, conditon));
		sql.append(WHERE);
		sql.append(getConditonWithPlaceHolder(pkName, allDbColumName, conditon));

		return sql.toString();
	}

	public String toSqlString(String pkName, String tableName, String[] allDbColumName, String[] target, String... conditon) {
		StringBuffer sql = new StringBuffer();
		sql.append(UPDATE);
		sql.append(SINGLE_QUOTE + tableName + SINGLE_QUOTE);
		sql.append(SET);
		String[] tString;
		if (target != null && target.length > 0) {
			tString = target;
		} else {
			tString = allDbColumName;
		}

		sql.append(getColumWithPlaceHolder(pkName, tString, conditon));
		sql.append(WHERE);
		sql.append(getConditonWithPlaceHolder(pkName, tString, conditon));

		return sql.toString();
	}

}
