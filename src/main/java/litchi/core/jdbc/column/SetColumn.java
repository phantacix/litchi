//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.jdbc.column;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.esotericsoftware.reflectasm.FieldAccess;

import litchi.core.jdbc.table.Table;
import litchi.core.jdbc.table.TableInfo;

/**
 *
 * @author paopao
 * 2020-03-26
 */
public class SetColumn extends AbstractColumnParser {
    @Override
    public void readColumn(Table<?> instance, TableInfo.TableColumnInfo columnInfo, ResultSet rs) throws SQLException {
        FieldAccess fieldAccess = instance.getTableInfo().fieldAccess;

        byte[] bytes = rs.getBytes(columnInfo.aliasName);
        if (bytes != null && bytes.length > 0) {
        	Class<?> valueType = columnInfo.getColumnType(1);
            List<?> value = JsonEntityParser.parseArray(bytes, valueType);
            @SuppressWarnings("unchecked")
            Set<Object> set = (Set<Object>) fieldAccess.get(instance, columnInfo.fieldName);
            if (set != null) {
                set.addAll(value);
            } else {
            	set = new CopyOnWriteArraySet<Object>();
            	set.addAll(value);
                fieldAccess.set(instance, columnInfo.fieldName, value);
            }
        }
    }

    @Override
    public void writeColumn(ArrayList<Object> writeList, TableInfo.TableColumnInfo columnInfo, Object fieldValue) {
        if (fieldValue == null) {
            writeList.add("");
        } else {
            String jsonString = JSON.toJSONString(fieldValue, SerializerFeature.IgnoreNonFieldGetter);
            writeList.add(jsonString);
        }
    }
}
