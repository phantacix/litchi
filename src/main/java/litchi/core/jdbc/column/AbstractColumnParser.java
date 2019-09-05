//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.jdbc.column;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import litchi.core.jdbc.table.Table;
import litchi.core.jdbc.table.TableInfo.TableColumnInfo;

public abstract class AbstractColumnParser {

	public abstract void readColumn(Table<?> instance, TableColumnInfo columnInfo, ResultSet rs) throws SQLException;

	public abstract void writeColumn(ArrayList<Object> writeList, TableColumnInfo columnInfo, Object fieldValue);
}
