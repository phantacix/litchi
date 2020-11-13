//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.jdbc.column;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.esotericsoftware.reflectasm.FieldAccess;
import litchi.core.jdbc.table.Table;
import litchi.core.jdbc.table.TableInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 0x737263
 */
public class MapColumn extends AbstractColumnParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(MapColumn.class);

    @SuppressWarnings("unchecked")
    @Override
    public void readColumn(Table<?> instance, TableInfo.TableColumnInfo columnInfo, ResultSet rs) throws SQLException {
        FieldAccess fieldAccess = instance.getTableInfo().fieldAccess;

        Map<Object, Object> originMaps = (Map<Object, Object>) fieldAccess.get(instance, columnInfo.fieldName);
        if (originMaps == null) {
            originMaps = new ConcurrentHashMap<>();
            fieldAccess.set(instance, columnInfo.fieldName, originMaps);
        }

        Class<?> keyType = columnInfo.columnTypeList.get(1);
        Class<?> valueType = columnInfo.columnTypeList.get(2);
        byte[] jsonBytes = rs.getBytes(columnInfo.aliasName);
        if (jsonBytes != null && jsonBytes.length > 0) {
            try {
                JSONObject jsonObject = JSON.parseObject(new String(jsonBytes));
                for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                    Object key;
                    Object obj = entry.getKey();
                    if (obj instanceof Number) {
                        Number number = (Number) obj;
                        if (keyType == Integer.class) {
                            key = number.intValue();
                        } else if (keyType == Long.class) {
                            key = number.longValue();
                        } else {
                            LOGGER.error("不支持的map数据类型：{}", keyType);
                            continue;
                        }
                    } else {
                        key = obj;
                    }

                    try {
                        Object value = JsonEntityParser.parseJson(entry.getValue().toString(), valueType);
                        originMaps.put(key, value);
                    } catch (Exception e) {
                        LOGGER.error("read column error. class={} field={} key={} value={}",
                                instance.getTableInfo().clazz().getSimpleName(), columnInfo.fieldName, key, entry.getValue().toString());
                        LOGGER.error("", e);
                    }
                }
            } catch (Exception ex) {
                LOGGER.info("{}", ex);
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
