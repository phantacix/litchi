//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.jdbc.column;

import com.alibaba.fastjson.JSON;
import com.esotericsoftware.reflectasm.FieldAccess;
import litchi.core.jdbc.table.JsonEntity;
import litchi.core.jdbc.table.Table;
import litchi.core.jdbc.table.TableInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author 0x737263
 */
public class JsonColumn extends AbstractColumnParser {
    @Override
    public void readColumn(Table<?> instance, TableInfo.TableColumnInfo columnInfo, ResultSet rs) throws SQLException {
        FieldAccess fieldAccess = instance.getTableInfo().fieldAccess;

        byte[] byteJson = rs.getBytes(columnInfo.aliasName);
        if (byteJson == null) {
            return;
        }

        Object obj = JSON.parseObject(byteJson, columnInfo.getColumnType(0));
        fieldAccess.set(instance, columnInfo.fieldName, obj);

        JsonEntity jsonEntity = (JsonEntity) fieldAccess.get(instance, columnInfo.fieldName);
        if (jsonEntity != null) {
            jsonEntity.afterRead(obj);
        }
    }

    @Override
    public void writeColumn(ArrayList<Object> writeList, TableInfo.TableColumnInfo columnInfo, Object fieldValue) {
        if (fieldValue == null) {
            writeList.add("");
        } else {
            JsonEntity jsonEntity = (JsonEntity) fieldValue;
            jsonEntity.beginWrite();
            writeList.add(JSON.toJSONBytes(fieldValue));
        }
    }
}
