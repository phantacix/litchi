package litchi.core.dbqueue;

import com.alibaba.fastjson.JSONObject;
import litchi.core.Constants;
import litchi.core.Litchi;
import litchi.core.common.thread.NamedScheduleExecutor;
import litchi.core.jdbc.FastJdbc;
import litchi.core.jdbc.table.Table;
import litchi.core.jdbc.table.TableInfo;
import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class SQLQueueComponent implements DBQueue {
    private static final Logger LOGGER= LoggerFactory.getLogger(SQLQueueComponent.class);

    private int dbPoolSize = Runtime.getRuntime().availableProcessors();
    private int tableSubmitFrequency = 6000;
    private int tableSubmitNum = 10;
    private int shutdownTableSubmitFrequency = 6000;
    private int shutdownTableSubmitNum = 10;

    /**
     * key:TableName,value:Queue<values>
     */
    private static ConcurrentHashMap<String, ConcurrentLinkedQueue<Object[]>> TABLE_QUEUE = new ConcurrentHashMap<>();

    /**
     * key:TableName, value:TableInfo
     */
    private static ConcurrentHashMap<String, TableInfo> TABLE_INFO = new ConcurrentHashMap<>();

    /**
     * key:TableName, value:isSubmit
     */
    private static ConcurrentHashMap<String, Boolean> TABLE_SUBMIT_FLAG = new ConcurrentHashMap<>();

    private Litchi litchi;
    private FastJdbc defaultJdbc;

    private byte[] syncLock = new byte[0];

    /** 队列线程执行器 */
    private NamedScheduleExecutor executor;
    private long lastSubmitTime = 0L;

    public SQLQueueComponent(Litchi litchi) {
        JSONObject queue = litchi.config(name());
        if (queue == null) {
            LOGGER.error("dbQueue node not found in litchi.json");
            return;
        }

        int dbPoolSize = queue.getInteger("dbPoolSize");
        int tableSubmitFrequency = queue.getInteger("tableSubmitFrequency");
        int tableSubmitNum = queue.getInteger("tableSubmitNum");
        int shutdownTableSubmitFrequency = queue.getInteger("shutdownTableSubmitFrequency");
        int shutdownTableSubmitNum = queue.getInteger("shutdownTableSubmitNum");

        loadConfig(litchi, dbPoolSize, tableSubmitFrequency, tableSubmitNum, shutdownTableSubmitFrequency, shutdownTableSubmitNum);
    }

    public SQLQueueComponent(Litchi litchi, int dbPoolSize, int tableSubmitFrequency, int tableSubmitNum, int shutdownTableSubmitFrequency, int shutdownTableSubmitNum) {
        loadConfig(litchi, dbPoolSize, tableSubmitFrequency, tableSubmitNum, shutdownTableSubmitFrequency, shutdownTableSubmitNum);
    }

    private void loadConfig(Litchi litchi, int dbPoolSize, int tableSubmitFrequency, int tableSubmitNum, int shutdownTableSubmitFrequency, int shutdownTableSubmitNum) {
        this.litchi = litchi;
        if (dbPoolSize > 0) {
            this.dbPoolSize = dbPoolSize;
        }
        if (tableSubmitFrequency > 0) {
            this.tableSubmitFrequency = tableSubmitFrequency;
        }
        if (tableSubmitNum > 0) {
            this.tableSubmitNum = tableSubmitNum;
        }
        if (shutdownTableSubmitFrequency > 0) {
            this.shutdownTableSubmitFrequency = shutdownTableSubmitFrequency;
        }
        if (shutdownTableSubmitNum > 0) {
            this.shutdownTableSubmitNum = shutdownTableSubmitNum;
        }

        this.executor = new NamedScheduleExecutor(this.dbPoolSize, "dbQueue-queue-thread");
    }

    protected ConcurrentLinkedQueue<Object[]> getQueue(Table<?> table) {
        synchronized (syncLock) {
            ConcurrentLinkedQueue<Object[]> queue = TABLE_QUEUE.get(table.tableName());
            if (queue == null) {
                queue = new ConcurrentLinkedQueue<>();
                TABLE_QUEUE.putIfAbsent(table.tableName(), queue);
                TABLE_INFO.putIfAbsent(table.tableName(), table.getTableInfo());
            }
            return queue;
        }
    }

    @Override
    public void updateQueue(Table<?>... tables) {
        for (Table<?> table : tables) {
            try {
                ConcurrentLinkedQueue<Object[]> queue = getQueue(table);
                queue.add(table.writeData());
            } catch (Exception ex) {
                LOGGER.error("Table into queue error. {}", ex);
                LOGGER.error("tableName:{}, values:{}", table.tableName(),table.writeData());
            }
        }
    }

    @Override
    public void updateQueue(Collection<Table<?>> tables) {
        for (Table<?> table : tables) {
            updateQueue(table);
        }
    }

    @Override
    public int getTaskSize() {
        return executor.getQueue().size();
    }

    @Override
    public int getTableSize() {
        int size = 0;
        for (Map.Entry<String, ConcurrentLinkedQueue<Object[]>> entry : TABLE_QUEUE.entrySet()) {
            size += entry.getValue().size();
        }
        return size;
    }

    @Override
    public void changeSubmitNum(int newSubmitNum) {
        if (newSubmitNum < 1) {
            LOGGER.warn("change submit num. new value must than 1. value:{}", newSubmitNum);
            return;
        }
        LOGGER.info("change submit num. origin:{}, new:{}", this.tableSubmitNum, newSubmitNum);

        this.tableSubmitNum = newSubmitNum;
    }

    @Override
    public void changeSubmitFrequency(int newSubmitFrequency) {
        if (newSubmitFrequency < 1000) {
            LOGGER.warn("change submit frequency. new value must than 1000ms. value:{}", newSubmitFrequency);
            return;
        }

        LOGGER.info("change submit frequency. origin:{}ms, new:{}ms", this.tableSubmitFrequency, newSubmitFrequency);
        this.tableSubmitFrequency = newSubmitFrequency;
    }

    @Override
    public boolean inQueue(Table<?> table) {
        LOGGER.error("inQueue Method not implemented");
        return false;
    }

    @Override
    public String name() {
        return Constants.Component.DB_QUEUE;
    }

    @Override
    public void start() {
        this.defaultJdbc = litchi.getComponent(FastJdbc.class);
        if (this.defaultJdbc == null) {
            LOGGER.error("jdbc factory is null");
            return;
        }
    }

    @Override
    public void afterStart() {
        LOGGER.info("initialize dbQueue daemon thread...");

        // 单线程负责调度
        executor.scheduleAtFixedRate(() -> {
            try {
                if (System.currentTimeMillis() > this.lastSubmitTime) {
                    this.lastSubmitTime = System.currentTimeMillis() + this.tableSubmitFrequency;

                    for (Map.Entry<String, ConcurrentLinkedQueue<Object[]>> entry : TABLE_QUEUE.entrySet()) {

                        Boolean flag = TABLE_SUBMIT_FLAG.get(entry.getKey());
                        if (flag != null && flag == true) {
                            continue;
                        }

                        TABLE_SUBMIT_FLAG.put(entry.getKey(), true);
                        //submit runnable
                        this.executor.submit(()-> executeQueue(entry.getKey()));
                    }
                }
            } catch (Exception ex) {
                LOGGER.error("{}", ex);
            }
        }, 0, 50, TimeUnit.MILLISECONDS);

        LOGGER.info("jdbcId queue is started...");
    }

    private void executeQueue(String tableName) {

        try {
            ConcurrentLinkedQueue<Object[]> queue = TABLE_QUEUE.get(tableName);

            final TableInfo tableInfo = TABLE_INFO.get(tableName);
            final String sql = FastJdbc.replaceSql.toSqlString(tableInfo.annotation().tableName(), tableInfo.buildDbColumns());

            for (int i = 0; i < this.tableSubmitNum; i++) {
                Object[] values = queue.poll();
                if (values == null) {
                    continue;
                }

                try {
                    QueryRunner runner = defaultJdbc.getJdbcTemplate(tableInfo.clazz());
                    runner.execute(sql, values);
                } catch (Exception ex) {
                    LOGGER.error("submit table error. {}", ex);
                    LOGGER.error("sql :{}", sql);
                    LOGGER.error("sql value:{}", values);
                }
            }
        } finally {
            TABLE_SUBMIT_FLAG.put(tableName, false);
        }
    }

    @Override
    public void stop() {
        LOGGER.info("shutdown hook dbQueue thread ready...");

        // 修改为停服时的提交数量
        this.tableSubmitFrequency = this.shutdownTableSubmitFrequency;
        this.tableSubmitNum = this.shutdownTableSubmitNum;

        while (!executor.isShutdown()) {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                LOGGER.error("{}", e);
            }

            int entitySize = getTableSize();
            LOGGER.info("executor queue num:{}, task Count:{}, dbQueue cache size:{}", getTaskSize(), executor.getTaskCount(), entitySize);
            if (getTaskSize() <= 1 && entitySize == 0) {
                try {
                    executor.shutdown();
                    executor.awaitTermination(1800L, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    LOGGER.error("{}", e);
                }
            }
        }
        LOGGER.info("DefaultDBQueue shutdown complete!.....");
    }
}
