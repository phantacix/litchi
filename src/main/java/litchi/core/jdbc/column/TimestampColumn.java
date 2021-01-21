package litchi.core.jdbc.column;

import com.esotericsoftware.reflectasm.FieldAccess;
import litchi.core.jdbc.table.Table;
import litchi.core.jdbc.table.TableInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class TimestampColumn extends AbstractColumnParser {
    @Override
    public void readColumn(Table<?> instance, TableInfo.TableColumnInfo columnInfo, ResultSet rs) throws SQLException {
        FieldAccess fieldAccess = instance.getTableInfo().fieldAccess;
        Timestamp x = rs.getTimestamp(columnInfo.aliasName);
        fieldAccess.set(instance, columnInfo.fieldName, x);
    }

    @Override
    public void writeColumn(ArrayList<Object> writeList, TableInfo.TableColumnInfo columnInfo, Object fieldValue) {
        if (fieldValue == null) {
            writeList.add("");
        } else {
            writeList.add(fieldValue.toString());
        }
    }
}