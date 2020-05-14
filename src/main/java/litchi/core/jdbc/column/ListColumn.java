//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.jdbc.column;

import com.alibaba.fastjson.JSON;
import com.esotericsoftware.reflectasm.FieldAccess;
import litchi.core.jdbc.table.Table;
import litchi.core.jdbc.table.TableInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 0x737263
 */
public class ListColumn extends AbstractColumnParser {
    @Override
    public void readColumn(Table<?> instance, TableInfo.TableColumnInfo columnInfo, ResultSet rs) throws SQLException {
        FieldAccess fieldAccess = instance.getTableInfo().fieldAccess;

        byte[] jsonBytes = rs.getBytes(columnInfo.aliasName);
        if (jsonBytes != null && jsonBytes.length > 0) {
            Class<?> valueType = columnInfo.getColumnType(1);
            List<?> value = JsonEntityParser.parseArray(jsonBytes, valueType);
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) fieldAccess.get(instance, columnInfo.fieldName);
            if (list != null) {
                list.addAll(value);
            } else {
                fieldAccess.set(instance, columnInfo.fieldName, value);
            }
        }
    }

    @Override
    public void writeColumn(ArrayList<Object> writeList, TableInfo.TableColumnInfo columnInfo, Object fieldValue) {
        if (fieldValue == null) {
            writeList.add("");
        } else {
            String jsonString = JSON.toJSONString(fieldValue);
            writeList.add(jsonString);
        }
    }
}
