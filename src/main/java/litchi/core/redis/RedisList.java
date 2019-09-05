//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.redis;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.parser.ParserConfig;

import redis.clients.jedis.Client;
import redis.clients.jedis.Jedis;

/**
 * @author phil.shen on 2018/3/19
 **/
public class RedisList {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RedisList.class);

    private Jedis jedis;
    private boolean autoClose = true;
    private ParserConfig autoTypeParserConfig = new ParserConfig();

    public RedisList(Jedis jedis) {
        this.jedis = jedis;
        this.autoTypeParserConfig.setAutoTypeSupport(true);
    }
    
    /**
	 * 需要进行多条命令操作时，调用此方法可以关闭自动资源释放，操作完成后需手动调用close()进行资源释放
	 * @return
	 */
	public RedisList multipleCommand() {
		autoClose = false;
		return this;
	}

    /**
     * 移出并获取列表的第一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
     * @param timeout
     * @param keys
     * @return
     */
    public List<String> blpop(int timeout, String... keys) {
    	try {
    		return jedis.blpop(timeout, keys);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if (autoClose) {
				close();
			}
		}
    	return Collections.emptyList();
    }

    /**
     * 移出并获取列表的最后一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
     * @param timeout
     * @param keys
     * @return
     */
    public List<String> brpop(int timeout, String... keys) {
        try {
        	return jedis.brpop(timeout, keys);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if (autoClose) {
				close();
			}
		}
    	return Collections.emptyList();
    }

    /**
     * 从列表中弹出一个值，将弹出的元素插入到另外一个列表中并返回它； 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
     * @param source
     * @param destination
     * @param timeout
     * @return
     */
    public String brpopLPush(String source, String destination, int timeout) {
        try {
        	return jedis.brpoplpush(source, destination, timeout);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if (autoClose) {
				close();
			}
		}
    	return "";
    }

    /**
     * 通过索引获取列表中的元素
     * @param key
     * @param index
     * @return
     */
    public String index(String key, int index) {
        try {
        	return jedis.lindex(key, index);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if (autoClose) {
				close();
			}
		}
    	return "";
    }

    /**
     * 在列表的元素前或者后插入元素
     * @param key
     * @param where
     * @param pivot
     * @param value
     * @return
     */
    public Long insert(String key, Client.LIST_POSITION where, String pivot, String value) {
        try {
        	return jedis.linsert(key, where, pivot, value);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if (autoClose) {
				close();
			}
		}
    	return -1L;
    }

    /**
     * 获取列表长度
     * @param key
     * @return
     */
    public Long len(String key) {
        try {
        	return jedis.llen(key);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if (autoClose) {
				close();
			}
		}
    	return -1L;
    }

    /**
     * 移出并获取列表的第一个元素
     * @param key
     * @return
     */
    public String pop(String key) {
        try {
        	return jedis.lpop(key);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if (autoClose) {
				close();
			}
		}
    	return "";
    }

    /**
     * 将一个或多个值插入到列表头部
     * @param key
     * @param values
     * @return
     */
    public Long push(String key, String... values) {
        try {
        	return jedis.lpush(key, values);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if (autoClose) {
				close();
			}
		}
    	return -1L;
    }

    /**
     * 获取列表指定范围内的元素
     * @param key
     * @param start
     * @param stop
     * @return
     */
    public List<String> range(String key, long start, long stop) {
        try {
        	return jedis.lrange(key, start, stop);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if (autoClose) {
				close();
			}
		}
    	return Collections.emptyList();
    }

    /**
     * 移除列表元素
     * @param key
     * @param count
     * @param value
     * @return
     */
    public Long rem(String key, int count, String value) {
        try {
        	return jedis.lrem(key, count, value);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if (autoClose) {
				close();
			}
		}
    	return -1L;
    }

    /**
     * 通过索引设置列表元素的值
     * @param key
     * @param index
     * @param value
     * @return
     */
    public String set(String key, int index, String value) {
        try {
        	return jedis.lset(key, index, value);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if (autoClose) {
				close();
			}
		}
    	return "";
    }

    /**
     * 对一个列表进行修剪(trim)，就是说，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除。
     * @param key
     * @param start
     * @param stop
     * @return
     */
    public String trim(String key, long start, long stop) {
        try {
        	return jedis.ltrim(key, start, stop);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if (autoClose) {
				close();
			}
		}
    	return "";
    }

    /**
     * 移除并获取列表最后一个元素
     * @param key
     * @return
     */
    public String rpop(String key) {
        try {
        	return jedis.rpop(key);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if (autoClose) {
				close();
			}
		}
    	return "";
    }

    /**
     * 移除列表的最后一个元素，并将该元素添加到另一个列表并返回
     * @param source
     * @param destination
     * @return
     */
    public String rpopLpush(String source, String destination) {
        try {
        	return jedis.rpoplpush(source, destination);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if (autoClose) {
				close();
			}
		}
    	return "";
    }

    /**
     * 在列表中添加一个或多个值
     * @param key
     * @param values
     * @return
     */
    public Long rpush(String key, String... values) {
        try {
        	return jedis.rpush(key, values);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if (autoClose) {
				close();
			}
		}
    	return -1L;
    }

    /**
     * 为已存在的列表添加值
     * @param key
     * @param value
     * @return
     */
    public Long rpushx(String key, String value) {
        try {
        	return jedis.rpushx(key, value);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if (autoClose) {
				close();
			}
		}
    	return -1L;
    }
    
    public void close() {
		jedis.close();
	}
}
