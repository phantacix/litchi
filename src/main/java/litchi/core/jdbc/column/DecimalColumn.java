//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.jdbc.column;

import com.esotericsoftware.reflectasm.FieldAccess;
import litchi.core.jdbc.table.Table;
import litchi.core.jdbc.table.TableInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author 0x737263
 */
public class DecimalColumn extends AbstractColumnParser {
    @Override
    public void readColumn(Table<?> instance, TableInfo.TableColumnInfo columnInfo, ResultSet rs) throws SQLException {
        FieldAccess fieldAccess = instance.getTableInfo().fieldAccess;
        fieldAccess.set(instance, columnInfo.fieldName, rs.getObject(columnInfo.aliasName));
    }

    @Override
    public void writeColumn(ArrayList<Object> writeList, TableInfo.TableColumnInfo columnInfo, Object fieldValue) {
        writeList.add(fieldValue);
    }
}
