//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.dbqueue;

import com.alibaba.fastjson.JSONObject;
import litchi.core.Constants;
import litchi.core.Litchi;
import litchi.core.common.thread.NamedScheduleExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import litchi.core.jdbc.FastJdbc;
import litchi.core.jdbc.table.Table;
import litchi.core.jdbc.table.TableInfo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * 默认的DB队列实现
 * @author 0x737263
 *
 */
public class DBQueueComponent implements DBQueue {
	private static final Logger LOGGER = LoggerFactory.getLogger(DBQueueComponent.class);
	/** db队列日志记录于 logs/dbqueue.log */
	private static final Logger DB_QUEUE_LOGGER = LoggerFactory.getLogger("dbqueue");

	private int dbPoolSize = Runtime.getRuntime().availableProcessors();
	private int tableSubmitFrequency = 6000;
	private int tableSubmitNum = 10;
	private int shutdownTableSubmitFrequency = 6000;
	private int shutdownTableSubmitNum = 10;

	// ------------------------------------------------------------------------------

	private FastJdbc defaultJdbc;

	private DBEntity2File dbEntity2File;

	/** 队列线程执行器 */
	private NamedScheduleExecutor executor;

	/** 在队列中的主键关联集合  key:tableName,value:pk value */
	private ConcurrentHashMap<String, Set<Object>> pkMaps = new ConcurrentHashMap<>();
	/** table队列 */
	private ConcurrentLinkedQueue<Table<?>> tableQueue = new ConcurrentLinkedQueue<>();
	
	private byte[] syncLock = new byte[0];

	private long lastSubmitTime = 0L;

	private Litchi litchi;

	public DBQueueComponent(Litchi litchi) {
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

	public DBQueueComponent(Litchi litchi, int dbPoolSize, int tableSubmitFrequency, int tableSubmitNum, int shutdownTableSubmitFrequency, int shutdownTableSubmitNum) {
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

	@Override
	public void updateQueue(Table<?>... tables) {
		for (Table<?> table : tables) {
			synchronized (syncLock) {
				Set<Object> sets = pkMaps.get(table.tableName());
				if (sets == null) {
					sets = Collections.synchronizedSet(new HashSet<>());
					pkMaps.putIfAbsent(table.tableName(), sets);
				}

				if (!sets.contains(table.getPkId())) {
					sets.add(table.getPkId());
				}

				// add to queue
				tableQueue.add(table);
			}
		}
	}

	@Override
	public void updateQueue(Collection<Table<?>> tables) {
		for (Table<?> table : tables) {
			updateQueue(table);
		}
	}

	private void consumeTable() {
		if (tableQueue.isEmpty()) {
			return;
		}

		List<Table<?>> submitTables = new ArrayList<>();

		for (int i = 0; i < this.tableSubmitNum; i++) {
			Table<?> entry = tableQueue.poll();
			if (entry == null) {
				break;
			}
			submitTables.add(entry);
		}

		if (submitTables.isEmpty()) {
			return;
		}

		executor.execute(() -> {
			List<Table<?>> failTables = new ArrayList<>();

			submitTables.forEach(table -> {

				TableInfo info = table.getTableInfo();
				try {
					defaultJdbc.update(table);

					// 移除pk键值					
					Set<Object> sets = pkMaps.get(table.tableName());
					if (sets != null) {
						sets.remove(table.getPkId());
					}
				} catch (Exception e) {
					LOGGER.error("Exception. game:{}", Arrays.toString(table.writeData()));
					LOGGER.error("Exception. game info:{}", info);

//					if (e instanceof DataAccessException) {
//						// 数据库访问异常重新加入队列
//						failTables.add(table);
//						LOGGER.error("{}", e);
//					} else {
//
//					}

					dbEntity2File.write(table, table.tableName());
					LOGGER.error("{}", e);
				}
			});

			// 重新提交到队列,有可能造成旧数据覆盖新数据
			failTables.forEach(table -> {
				if (!inQueue(table)) {
					updateQueue(table);
				}
			});

			if (DB_QUEUE_LOGGER.isDebugEnabled()) {
				DB_QUEUE_LOGGER.debug("submit num:{}, fail num:{}", submitTables.size(), failTables.size());
			}
		});
	}

	/**
	 * 获取任务队列任务数
	 * @return
	 */
	@Override
	public int getTaskSize() {
		return executor.getQueue().size();
	}

	@Override
	public int getTableSize() {
		return this.tableQueue.size();
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
		if (tableQueue.contains(table)) {
			return true;
		}

		Set<Object> sets = pkMaps.get(table.tableName());
		if (sets == null) {
			return false;
		}

		return sets.contains(table.getPkId());
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

		this.dbEntity2File = new DBEntity2File();

		// 定时入库线程
		executor.scheduleAtFixedRate(() -> {
			try {
				if (System.currentTimeMillis() > this.lastSubmitTime) {
					this.lastSubmitTime = System.currentTimeMillis() + this.tableSubmitFrequency;
					consumeTable();
				}
			} catch (Exception ex) {
				LOGGER.error("{}", ex);
			}
		}, 0, 50, TimeUnit.MILLISECONDS);

		LOGGER.info("jdbcId queue is started...");
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
