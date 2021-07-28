//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.jdbc.table;

import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 0x737263
 */
public class TableListHandler<T> implements ResultSetHandler<List<T>> {

    private Class<?> type;

    public TableListHandler(Class<? extends Table> type) {
        this.type = type;
    }

    @Override
    public List<T> handle(ResultSet rs) throws SQLException {
        List<T> results = new ArrayList<>();
        if (!rs.next()) {
            return results;
        }
        try {
            Table<?> instance = (Table<?>) type.newInstance();
            do {
                results.add((T) instance.handle(rs));
            } while (rs.next());
        } catch (Exception ex) {
        }
        return results;
    }
}
