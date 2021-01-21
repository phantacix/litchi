//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.jdbc.column;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import litchi.core.jdbc.table.JsonEntity;

public class ColumnContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(ColumnContext.class);

    private static Map<Class<?>, AbstractColumnParser> PARSER_MAP = new HashMap<>();


    public ColumnContext() {

    }

    private void register(AbstractColumnParser parser, Class<?>... classes) {
        for (Class<?> clazz : classes) {
            PARSER_MAP.put(clazz, parser);
        }
    }

    /**
     * 获取解析器
     *
     * @param clazz
     * @return
     */
    public static AbstractColumnParser getParser(Class<?> clazz) {
        if (JsonEntity.class.isAssignableFrom(clazz)) {
            return PARSER_MAP.get(JsonEntity.class);
        }

        return PARSER_MAP.get(clazz);
    }

    public void init() {
        register(new AtomicLongColumn(), AtomicLong.class);
        register(new BooleanColumn(), Boolean.class, boolean.class);
        register(new NumberColumn(), Byte.class, byte.class, int.class, Integer.class, Long.class, long.class, Short.class, short.class);
        register(new DoubleColumn(), Double.class, double.class);
        register(new FloatColumn(), Float.class, float.class);
        register(new StringColumn(), String.class);
        register(new DecimalColumn(), BigDecimal.class);
        register(new ByteArrayColumn(), byte[].class);
        register(new ListColumn(), List.class);
        register(new SetColumn(), Set.class);
        register(new MapColumn(), Map.class);
        // register(new BlobColumn(), ProtoEntity.class);
        // register(new BlobTypeColumn(), ProtoTypeEntity.class);
        register(new JsonColumn(), JsonEntity.class);
        register(new DateColumn(), Date.class);
        register(new TimestampColumn(), Timestamp.class);

        LOGGER.info("ColumnContext parser start complete!");
    }
}
