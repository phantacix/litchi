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
public class NumberColumn extends AbstractColumnParser {
    @Override
    public void readColumn(Table<?> instance, TableInfo.TableColumnInfo columnInfo, ResultSet rs) throws SQLException {
        try {
            FieldAccess fieldAccess = instance.getTableInfo().fieldAccess;
            Object x = rs.getObject(columnInfo.aliasName);
            fieldAccess.set(instance, columnInfo.fieldName, x);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeColumn(ArrayList<Object> writeList, TableInfo.TableColumnInfo columnInfo, Object fieldValue) {
        writeList.add(fieldValue.toString());
    }
}
