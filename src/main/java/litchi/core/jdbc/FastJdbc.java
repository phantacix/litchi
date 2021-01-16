//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.jdbc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zaxxer.hikari.HikariDataSource;
import litchi.core.Constants;
import litchi.core.Litchi;
import litchi.core.components.Component;
import litchi.core.jdbc.column.ColumnContext;
import litchi.core.jdbc.sql.*;
import litchi.core.jdbc.table.SuperTable;
import litchi.core.jdbc.table.Table;
import litchi.core.jdbc.table.TableInfo;
import litchi.core.jdbc.table.TableListHandler;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.AbstractListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author 0x737263
 */
public class FastJdbc implements Component {
    private static final Logger LOGGER = LoggerFactory.getLogger(FastJdbc.class);

    public static SelectStatement selectSql = new SelectStatement();
    public static DeleteStatement delSql = new DeleteStatement();
    public static InsertStatement insertSql = new InsertStatement();
    public static ReplaceIntoStatement replaceSql = new ReplaceIntoStatement();
    public static UpdateStatement updateSql = new UpdateStatement();

    /**
     * key:dbType,value:jdbc
     */
    private Map<String, QueryRunner> jdbcMaps = new HashMap<>();

    private Litchi litchi;

    public FastJdbc(Litchi litchi) {
        this.litchi = litchi;

        JSONArray jdbcConfigs = litchi.config().getJSONArray(name());
        if (jdbcConfigs == null) {
            LOGGER.error("jdbc node not found in litchi.json");
            return;
        }

        JSONArray jdbcIdList = litchi.currentNode().getJsonArrayOpts("jdbcIds");
        if (jdbcIdList == null) {
            LOGGER.error("jdbcIds node is null in nodes.json.");
            return;
        }

        for (int id = 0; id < jdbcIdList.size(); id++) {
            String jdbcId = jdbcIdList.getString(id);

            for (int i = 0; i < jdbcConfigs.size(); i++) {
                JSONObject config = jdbcConfigs.getJSONObject(i);
                if (config.getString("id").equals(jdbcId)) {
                    addJdbc(config);
                }
            }
        }
    }

    public void addJdbc(JSONObject config) {
        String dbType = config.getString("dbType");
        String dbName = config.getString("dbName");
        String host = config.getString("host");
        String userName = config.getString("userName");
        String password = config.getString("password");
//      int initialSize = config.getInteger("initialSize");
//      int maxActive = config.getInteger("maxActive");
//      int maxIdle = config.getInteger("maxIdle");
//      int minIdle = config.getInteger("minIdle");

        HikariDataSource ds = new HikariDataSource();
        String jdbc = "jdbc:mysql://%s/%s?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC&useSSL=false";

        ds.setJdbcUrl(String.format(jdbc, host, dbName));
        ds.setUsername(userName);
        ds.setPassword(password);
        ds.addDataSourceProperty("cachePrepStmts", "true");
        ds.addDataSourceProperty("prepStmtCacheSize", "500");
        ds.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        QueryRunner queryRunner = new QueryRunner(ds);
        jdbcMaps.put(dbType, queryRunner);

        LOGGER.info("add jdbcId = {}", config.getString("id"));
    }

    public boolean isConnectJdbc(String dbType) {
        return jdbcMaps.containsKey(dbType);
    }

    public <T extends Table<?>> QueryRunner getJdbcTemplate(Class<T> tableClazz) {
        TableInfo tableInfo = SuperTable.getTableInfo(tableClazz);

        QueryRunner queryRunner = jdbcMaps.get(tableInfo.annotation().dbType());
        if (queryRunner == null) {
            LOGGER.error("class:{} call jdbc is null", tableClazz);
        }
        return queryRunner;
    }

    public QueryRunner getJdbc(String dbType) {
        return jdbcMaps.get(dbType);
    }

    public <T extends Table<?>> int update(T table) {
        QueryRunner runner = getJdbcTemplate(table.getClass());

        TableInfo info = table.getTableInfo();
        String sql = replaceSql.toSqlString(table.tableName(), info.buildDbColumns());
        Object[] values = table.writeData();
        try {
            return runner.update(sql, values);
        } catch (Exception e) {
            LOGGER.error("", e);
            return 0;
        }
    }

    public <T extends Table<?>> int[] update(Collection<T> tables) {
        int[] result = new int[tables.size()];
        int i = 0;
        for (T table : tables) {
            result[i] = update(table);
            i++;
        }
        return result;
    }

    public <T extends Table<?>> int[] batchUpdate(Collection<T> tables) {
        if (tables == null || tables.isEmpty()) {
            return null;
        }

        QueryRunner runner = null;
        TableInfo info = null;
        String sql = null;

        try {

            Object[][] param = new Object[tables.size()][];

            int i = 0;
            for (T t : tables) {
                if (runner == null || info == null || sql == null) {
                    runner = getJdbcTemplate(t.getClass());
                    info = t.getTableInfo();
                    sql = replaceSql.toSqlString(t.tableName(), info.buildDbColumns());
                }

                param[i] = t.writeData();
                i++;
            }

            return runner.batch(sql, param);
        } catch (Exception e) {
            LOGGER.error("", e);
            return null;
        }
    }

    public <T extends Table<?>> int delete(T table) {
        try {
            QueryRunner runner = getJdbcTemplate(table.getClass());
            TableInfo info = table.getTableInfo();
            String sql = delSql.toSqlString(info.pkName(), info.annotation().tableName(), new String[]{info.pkName()});
            return runner.update(sql, new Object[]{table.getPkId()});
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return 0;
    }

    public <T extends Table<?>> int delete(Class<T> clazz, LinkedHashMap<String, Object> condition) {
        try {
            QueryRunner runner = getJdbcTemplate(clazz);
            TableInfo info = SuperTable.getTableInfo(clazz);
            String[] key = new String[condition.keySet().size()];
            String sql = delSql.toSqlString(info.pkName(), info.annotation().tableName(), condition.keySet().toArray(key));
            return runner.update(sql, condition.values().toArray());
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return 0;
    }

    public <T extends Table<?>> T get(Class<T> clazz, Object pk) {
        try {
            TableInfo info = SuperTable.getTableInfo(clazz);
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            map.put(info.pkName(), pk);
            return getFirst(clazz, map);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return null;
    }

    public <T extends Table<?>> T getFirst(Class<T> clazz, LinkedHashMap<String, Object> condition) {
        try {
            QueryRunner runner = getJdbcTemplate(clazz);
            TableInfo info = SuperTable.getTableInfo(clazz);
            String[] key = new String[condition.keySet().size()];
            String sql = selectSql.toSqlString(info.pkName(), info.annotation().tableName(), info.buildDbColumns(), condition.keySet().toArray(key));
            //List<T> result = (List<T>) runner.query(sql, info.newInstance(), condition.values().toArray());
            List<T> list = runner.query(sql, new TableListHandler<>(clazz), condition.values().toArray());
            if (list.isEmpty()) {
                return null;
            }
            return list.get(0);
        } catch (Exception e) {
            LOGGER.error("", e);
            return null;
        }
    }

    public <T extends Table<?>> T getFirst(Class<T> clazz, String tableSuffix, LinkedHashMap<String, Object> condition) {
        try {
            QueryRunner runner = getJdbcTemplate(clazz);
            TableInfo info = SuperTable.getTableInfo(clazz);
            String[] key = new String[condition.keySet().size()];
            String sql = selectSql.toSqlString(info.pkName(), info.annotation().tableName() + tableSuffix, info.buildDbColumns(), condition.keySet().toArray(key));
            List<T> list = runner.query(sql, new TableListHandler<T>(clazz), condition.values().toArray());
            return list.get(0);
        } catch (Exception e) {
            LOGGER.error("", e);
            return null;
        }
    }

    public <T extends Table<?>> List<T> getList(Class<T> clazz) {
        try {
            QueryRunner runner = getJdbcTemplate(clazz);
            TableInfo info = SuperTable.getTableInfo(clazz);
            String sql = selectSql.toSqlString(info.pkName(), info.annotation().tableName(), info.buildDbColumns());

            return runner.query(sql, new TableListHandler<T>(clazz));
        } catch (Exception e) {
            LOGGER.error("", e);
            return null;
        }
    }

    public <T extends Table<?>> List<T> getList(Class<T> clazz, String field, Object value) {
        LinkedHashMap<String, Object> condition = new LinkedHashMap<>();
        condition.put(field, value);
        return getList(clazz, condition);
    }

    @SuppressWarnings("unchecked")
    public <T extends Table<?>> List<T> getList(Class<T> clazz, LinkedHashMap<String, Object> condition) {
        try {
            QueryRunner runner = getJdbcTemplate(clazz);
            TableInfo info = SuperTable.getTableInfo(clazz);
            String[] key = new String[condition.keySet().size()];
            String sql = selectSql.toSqlString(info.pkName(), info.annotation().tableName(), info.buildDbColumns(), condition.keySet().toArray(key));
            //return (List<T>) runner.query(sql, condition.values().toArray(), info.newInstance());
            return runner.query(sql, new TableListHandler<>(clazz), condition.values().toArray());

        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return null;
    }

    public <T extends Table<?>> List<T> getList(Class<T> clazz, LinkedHashMap<String, Object> condition, String userDefinedCondition,
                                                List<Object> userDefinedParams) {
        try {
            QueryRunner runner = getJdbcTemplate(clazz);
            TableInfo info = SuperTable.getTableInfo(clazz);
            String[] key = new String[condition.keySet().size()];
            String[] array = condition.keySet().toArray(key);
            String sql = selectSql.toSqlString(info.pkName(), info.annotation().tableName(), info.buildDbColumns(), array, userDefinedCondition);
            Object[] args = condition.values().toArray();
            int uParamsNum = 0;
            if (userDefinedParams != null) {
                uParamsNum = userDefinedParams.size();
            }
            Object[] allArgs = Arrays.copyOf(args, args.length + uParamsNum);
            for (int i = 0; i < uParamsNum; i++) {
                allArgs[args.length + i] = userDefinedParams.get(i);
            }

            //return (List<T>) runner.query(sql, allArgs, info.newInstance());
            return runner.query(sql, new TableListHandler<>(clazz), allArgs);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return null;
    }

    public <T extends Table<?>> List<T> getList(Class<T> clazz, LinkedHashMap<String, Object> condition, int limitBegin, int limitEnd) {
        try {
            QueryRunner runner = getJdbcTemplate(clazz);
            TableInfo info = SuperTable.getTableInfo(clazz);
            String[] key = new String[condition.keySet().size()];
            String sql = selectSql.toSqlString(info.pkName(), info.annotation().tableName(),
                    info.buildDbColumns(), limitBegin, limitEnd, condition.keySet().toArray(key));
            if (limitBegin > 0) {
                condition.put("limitBegin", limitBegin);
            }
            if (limitEnd > 0) {
                condition.put("limitEnd", limitEnd);
            }
            //Table<?> instance = info.classAccess.newInstance();
            //return (List<T>) runner.query(sql, condition.values().toArray(), instance);
            return runner.query(sql, new TableListHandler<>(clazz), condition.values().toArray());
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return null;
    }

    public <T extends Table<?>> List<T> getList(Class<T> clazz, LinkedHashMap<String, Object> condition, int limitBegin, int limitEnd, String orderByColumn, String orderByType) {
        try {
            QueryRunner runner = getJdbcTemplate(clazz);

            TableInfo info = SuperTable.getTableInfo(clazz);
            String[] key = new String[condition.keySet().size()];
            String sql = selectSql.toSqlString(info.pkName(), info.annotation().tableName(),
                    info.buildDbColumns(), limitBegin, limitEnd, orderByColumn, orderByType, condition.keySet().toArray(key));

            if (limitBegin > 0) {
                condition.put("limitBegin", limitBegin);
            }
            if (limitEnd > 0) {
                condition.put("limitEnd", limitEnd);
            }

            //Table<?> instance = info.classAccess.newInstance();
            //return (List<T>) runner.query(sql, condition.values().toArray(), instance);
            return runner.query(sql, new TableListHandler<>(clazz), condition.values().toArray());
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return null;
    }

    /**
     * 自定义sql查询
     *
     * @param sql    sql语句
     * @param values 条件值
     * @param clazz  对应实体类
     * @return
     */
    public <T extends Table<?>> List<T> getList(String sql, Object[] values, Class<T> clazz) {
        try {
            QueryRunner runner = getJdbcTemplate(clazz);
            //TableInfo info = SuperTable.getTableInfo(clazz);
            //return (List<T>) runner.query(sql, condition, info.newInstance());
            return runner.query(sql, new TableListHandler<>(clazz), values);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return null;
    }

    /**
     * 查询主键列表
     *
     * @param clazz     类型
     * @param condition 查询条件 key:字段名 value:查询值
     * @return
     */
    public <T extends Table<?>> List<Long> getPKList(Class<T> clazz, LinkedHashMap<String, Object> condition) {
        return getPKList(clazz, condition, 0, 0);
    }

    public <T extends Table<?>> List<Long> getPKList(Class<T> clazz, LinkedHashMap<String, Object> condition, int limitBegin, int limitEnd) {
        try {
            QueryRunner runner = getJdbcTemplate(clazz);
            TableInfo info = SuperTable.getTableInfo(clazz);
            String[] key = new String[condition.keySet().size()];
            String sql = selectSql.toSqlString(info.pkName(), info.annotation().tableName(),
                    info.buildDbColumns(), info.pkName(), limitBegin, limitEnd, condition.keySet().toArray(key));
            if (limitBegin > 0) {
                condition.put("limitBegin", limitBegin);
            }
            if (limitEnd > 0 && limitEnd > limitBegin) {
                condition.put("limitEnd", limitEnd);
            }

            //return runner.queryForList(sql, condition.values().toArray(), Long.class);
            // new BeanListHandler<>(Long.class)
            return runner.query(sql, new AbstractListHandler<Long>() {
                @Override
                protected Long handleRow(ResultSet rs) throws SQLException {
                    if (rs.next()) {
                        return rs.getLong(1);
                    }
                    return null;
                }
            }, condition);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return null;
    }

    public <T extends Table<Long>> Long saveAndIncreasePK(T table) {
        try {
            QueryRunner runner = getJdbcTemplate(table.getClass());

            TableInfo info = SuperTable.getTableInfo(table.getClass());
            final String sql = replaceSql.toSqlString(info.annotation().tableName(), info.buildDbColumns());
            final Object[] values = table.writeData();

            return runner.insert(sql, new ScalarHandler<>(), values);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return 0L;
    }

    public <T extends Table<Long>> void execute(Class<T> clazz, String sql) {
        try {
            QueryRunner runner = getJdbcTemplate(clazz);
            runner.execute(sql); // .execute(sql);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    public <T> List<T> query(Class<? extends Table> clazz, String sql, LinkedHashMap<String, Object> condition, ResultSetHandler<T> handler) {
        try {
            QueryRunner runner = getJdbcTemplate(clazz);
            //return runner.query(sql, condition.values().toArray(), rowMapper);
            return runner.execute(sql, handler, condition.values().toArray());
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return null;
    }

    public <T> T queryObject(Class<? extends Table> clazz, String sql, ResultSetHandler<T> handler) {
        try {
            QueryRunner runner = getJdbcTemplate(clazz);
            return runner.query(sql, handler);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return null;
    }

    public <T> T queryObject(Class<? extends Table> clazz, String sql, LinkedHashMap<String, Object> condition, ResultSetHandler<T> handler) {
        try {
            if (condition == null || condition.isEmpty()) {
                return queryObject(clazz, sql, handler);
            }

            QueryRunner runner = getJdbcTemplate(clazz);
            return runner.query(sql, handler, condition.values().toArray());
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return null;
    }

    public Long queryScalar(Class<? extends Table> clazz, String sql, LinkedHashMap<String, Object> condition) {
        try {
            QueryRunner runner = getJdbcTemplate(clazz);
            return runner.query(sql, new ScalarHandler<Long>(), condition.values().toArray());
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return 0L;
    }


    @Override
    public String name() {
        return Constants.Component.JDBC;
    }

    @Override
    public void start() {
        ColumnContext context = new ColumnContext();
        context.init();
        SuperTable.tableScan(litchi.packagesName());
    }

    @Override
    public void afterStart() {
        LOGGER.info("jdbc factory is started!");
    }

    @Override
    public void stop() {
    }

    @Override
    public void beforeStop() {
    }
}
