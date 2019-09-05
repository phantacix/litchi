//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.jdbc.column;

import com.esotericsoftware.reflectasm.FieldAccess;
import litchi.core.jdbc.table.Table;
import litchi.core.jdbc.table.TableInfo;

import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author 0x737263
 */
public class StringColumn extends AbstractColumnParser {
    @Override
    public void readColumn(Table<?> instance, TableInfo.TableColumnInfo columnInfo, ResultSet rs) throws SQLException {
        String value = null;
        for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
            String columnName = rs.getMetaData().getColumnName(i);
            if (columnName.equals(columnInfo.aliasName)) {
                String columnTypeName = rs.getMetaData().getColumnTypeName(i);
                // BLOB/TEXT二进制存储结构都存在中文编码问题
                // BLOB/TEXT等二进制存储结构不能以UTF-8进行编码，需要将字节数组取出后进行UTF-8的编码转换
                if (columnTypeName.equals("BLOB") || columnTypeName.equals("TEXT")) {
                    byte[] bytes = rs.getBytes(columnInfo.aliasName);
                    value = new String(bytes, Charset.forName("UTF-8"));
                } else {
                    value = rs.getString(columnInfo.aliasName);
                }
                break;
            }
        }
        FieldAccess fieldAccess = instance.getTableInfo().fieldAccess;
        fieldAccess.set(instance, columnInfo.fieldName, value);
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
