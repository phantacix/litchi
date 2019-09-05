//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.redis;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanResult;

/**
 * @author phil.shen on 2018/3/19
 **/
public class RedisHash {

	private static final Logger LOGGER = LoggerFactory.getLogger(RedisHash.class);

	private Jedis jedis;
	private boolean autoClose = true;

	private ParserConfig autoTypeParserConfig = new ParserConfig();

	/**
	 * 需要进行多条命令操作时，调用此方法可以关闭自动资源释放，操作完成后需手动调用close()进行资源释放
	 * @return
	 */
	public RedisHash multipleCommand() {
		autoClose = false;
		return this;
	}

	public RedisHash(Jedis jedis) {
		this.jedis = jedis;
		this.autoTypeParserConfig.setAutoTypeSupport(true);
	}

	/**
	 * 删除一个或多个哈希表字段
	 * @param key
	 * @param fields
	 */
	public void del(String key, String... fields) {
		try {
			jedis.hdel(key, fields);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if (autoClose) {
				close();
			}
		}
	}

	/**
	 * 查看哈希表 key 中，指定的字段是否存在。
	 * @param key
	 * @param field
	 * @return
	 */
	public Boolean exists(String key, String field) {
		try {
			return jedis.hexists(key, field);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if (autoClose) {
				close();
			}
		}
		return false;
	}

	/**
	 * 获取存储在哈希表中指定字段的值
	 * @param key
	 * @param field
	 * @return
	 */
	public String get(String key, String field) {
		try {
			return jedis.hget(key, field);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if (autoClose) {
				close();
			}
		}
		return "";
	}

	public <T> T getObjectFromJson(String key, String field, Class<T> clazz) {
		return getObjectFromJson(key, field, clazz, false);
	}

	public <T> T getObjectFromJson(String key, String field, Class<T> clazz, boolean autoType) {
		try {
			String json = jedis.hget(key, field);
			if (json == null) {
				return null;
			}
			if (autoType) {
				return JSON.parseObject(json, clazz, autoTypeParserConfig);
			} else {
				return JSON.parseObject(json, clazz);
			}
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if (autoClose) {
				close();
			}
		}
		return null;
	}

	public Long setObjectToJson(String key, String field, Object obj, boolean autoType) {
		return setObjectToJson(key, field, obj, autoType, -1);
	}
	
	public Long setObjectToJson(String key, String field, Object obj, boolean autoType, int expireSeconds) {
		try {
			String value;
			if (autoType) {
				value = JSON.toJSONString(obj, SerializerFeature.IgnoreNonFieldGetter, SerializerFeature.WriteClassName);
			} else {
				value = JSON.toJSONString(obj, SerializerFeature.IgnoreNonFieldGetter);
				
			}
			Long hset = jedis.hset(key, field, value);
			if (expireSeconds > 0) {
				jedis.expire(key, expireSeconds);
			}
			return hset;
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if (autoClose) {
				close();
			}
		}
		return -1L;
	}

	public Long setObjectToJson(String key, String field, Object obj, int expireSeconds) {
		return setObjectToJson(key, field, obj, false, expireSeconds);
	}
	
	public Long setObjectToJson(String key, String field, Object obj) {
		return setObjectToJson(key, field, obj, false, -1);
	}

	/**
	 * 获取在哈希表中指定 key 的所有字段和值
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> getAll(String key) {
		try {
			return jedis.hgetAll(key);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if (autoClose) {
				close();
			}
		}
		return Collections.EMPTY_MAP;
	}

	public <T> Map<String, T> getAllObjectFromJson(String key, Class<T> tClass) {
		return getAllObjectFromJson(key, tClass, false);
	}
	
	public <T> Map<String, T> getAllObjectFromJson(String key, Class<T> tClass, boolean autoType) {
		Map<String, T> map = new HashMap<>();
		try {
			Map<String, String> hgetAll = jedis.hgetAll(key);
			for (Entry<String, String> entry : hgetAll.entrySet()) {
				T object;
				if (autoType) {
					object = JSON.parseObject(entry.getValue(), tClass, autoTypeParserConfig);
				} else {
					object = JSON.parseObject(entry.getValue(), tClass);
				}
				map.put(entry.getKey(), object);
			}
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if (autoClose) {
				close();
			}
		}
		return map;
	}

	/**
	 * 为哈希表 key 中的指定字段的整数值加上增量 increment 。
	 * @param key
	 * @param field
	 * @param increment
	 * @return
	 */
	public Long incrBy(String key, String field, long increment) {
		try {
			return jedis.hincrBy(key, field, increment);
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
	 * 为哈希表 key 中的指定字段的浮点数值加上增量 increment 。
	 * @param key
	 * @param field
	 * @param increment
	 * @return
	 */
	public Double incrByFloat(String key, String field, long increment) {
		try {
			return jedis.hincrByFloat(key, field, increment);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if (autoClose) {
				close();
			}
		}
		return -1d;
	}

	/**
	 * 获取所有哈希表中的字段
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<String> keys(String key) {
		try {
			return jedis.hkeys(key);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if (autoClose) {
				close();
			}
		}
		return Collections.EMPTY_SET;
	}

	/**
	 * 获取哈希表中字段的数量
	 * @param key
	 * @return
	 */
	public Long len(String key) {
		try {
			return jedis.hlen(key);
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
	 * 获取所有给定字段的值
	 * @param key
	 * @param fields
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<String> mget(String key, String... fields) {
		try {
			return jedis.hmget(key, fields);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if (autoClose) {
				close();
			}
		}
		return Collections.EMPTY_LIST;
	}

	/**
	 * 同时将多个 field-value (域-值)对设置到哈希表 key 中。
	 * @param key
	 * @param fieldValueMap
	 * @return
	 */
	public String mset(String key, Map<String, String> fieldValueMap) {
		try {
			return jedis.hmset(key, fieldValueMap);
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
	 * 将哈希表 key 中的字段 field 的值设为 value 。
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	public Long set(String key, String field, String value) {
		try {
			return jedis.hset(key, field, value);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if (autoClose) {
				close();
			}
		}
		return -1L;
	}

	public Long setWithExpireTime(String key, String field, String value, int seconds) {
		try {
			Long hset = jedis.hset(key, field, value);
			jedis.expire(key, seconds);
			return hset;
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
	 * 只有在字段 field 不存在时，设置哈希表字段的值。
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	public Long setNX(String key, String field, String value) {
		try {
			return jedis.hsetnx(key, field, value);
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
	 * 获取哈希表中所有值
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<String> vals(String key) {
		try {
			return jedis.hvals(key);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if (autoClose) {
				close();
			}
		}
		return Collections.EMPTY_LIST;
	}

	/**
	 * 迭代哈希表中的键值对。
	 * @param key
	 * @param cursor
	 * @return
	 */
	public ScanResult<Map.Entry<String, String>> scan(String key, String cursor) {
		try {
			return jedis.hscan(key, cursor);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if (autoClose) {
				close();
			}
		}
		return null;
	}

	public void close() {
		jedis.close();
	}

}
