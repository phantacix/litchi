//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.redis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

import redis.clients.jedis.Jedis;

/**
 * @author phil.shen on 2018/3/19
 **/
public class RedisKeyValue {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisKeyValue.class);

    private Jedis jedis;
    private ParserConfig autoTypeParserConfig = new ParserConfig();
    private boolean autoClose = true;

    public RedisKeyValue(Jedis jedis) {
        this.jedis = jedis;
        this.autoTypeParserConfig.setAutoTypeSupport(true);
    }
    
    /**
	 * 需要进行多条命令操作时，调用此方法可以关闭自动资源释放，操作完成后需手动调用close()进行资源释放
	 * @return
	 */
	public RedisKeyValue multipleCommand() {
		autoClose = false;
		return this;
	}

    /**
     * 获取存储在key中指定字段的值
     * @param key
     * @return
     */
    public String get(String key) {
        try {
        	return jedis.get(key);
        } catch (Exception e) {
            LOGGER.error("", e);
        } finally {
        	if (autoClose) {
        		jedis.close();
        	}
        }
        return "";
    }
    
    /**
     * 将 key 中 的值设为 value 。
     * @param key
     * @param value
     * @return
     */
    public String set(String key, String value) {
    	try {
    		return jedis.set(key, value);
    	} catch (Exception e) {
    		LOGGER.error("", e);
    	} finally {
    		if (autoClose) {
        		jedis.close();
        	}
    	}
    	return "";
    }
    
    /**
     * 将 key 中 的值设为 value 。
     * @param key
     * @param value
     * @return
     */
    public String set(String key, String value, int expireTime) {
        try {
            String result = jedis.set(key, value);
            jedis.expire(key, expireTime);
            return result;
        } catch (Exception e) {
            LOGGER.error("", e);
        } finally {
        	if (autoClose) {
        		jedis.close();
        	}
        }
        return "";
    }
    
    /**
     * 获取json数据并转换为对象
     * @param key
     * @param tClass
     * @return
     */
    public <T> T getObjectFromJson(String key, Class<T> tClass) {
    	String value = get(key);
    	return JSON.parseObject(value, tClass);
    }
    
    public <T> T getObjectFromJson(String key, Class<T> tClass, boolean autoType) {
    	String value = get(key);
    	if (autoType) {
    		return JSON.parseObject(value, tClass, autoTypeParserConfig);
		} else {
			return JSON.parseObject(value, tClass);
		}
    }
    
    /**
     * 将对象以json格式存入
     * @param key
     * @param obj
     * @return
     */
    public String setObjectToJson(String key, Object obj) {
    	String value = JSON.toJSONString(obj, SerializerFeature.IgnoreNonFieldGetter);
        return set(key, value);
    }
    
    public String setObjectToJson(String key, Object obj, boolean autoType) {
    	String value;
    	if (autoType) {
    		value = JSON.toJSONString(obj, SerializerFeature.IgnoreNonFieldGetter, SerializerFeature.WriteClassName);
		} else {
			value = JSON.toJSONString(obj, SerializerFeature.IgnoreNonFieldGetter);
		}
    	return set(key, value);
    }
    
    public String setObjectToJson(String key, Object obj, boolean autoType, int expireTime) {
    	String value;
    	if (autoType) {
    		value = JSON.toJSONString(obj, SerializerFeature.IgnoreNonFieldGetter, SerializerFeature.WriteClassName);
    	} else {
    		value = JSON.toJSONString(obj, SerializerFeature.IgnoreNonFieldGetter);
    	}
    	return set(key, value, expireTime);
    }
    
    public Long delete(String key) {
    	try {
    		return jedis.del(key);
        } catch (Exception e) {
            LOGGER.error("", e);
        } finally {
        	if (autoClose) {
        		jedis.close();
        	}
        }
        return -1L;
    }
    
    public List<String> getList(Set<String> keys) {
    	try {
    		String[] keyArray = new String[keys.size()];
    		int index = 0;
    		for (String key : keys) {
    			keyArray[index] = key;
    			index++;
			}
    		return jedis.mget(keyArray);
        } catch (Exception e) {
            LOGGER.error("", e);
        } finally {
        	if (autoClose) {
        		jedis.close();
        	}
        }
    	return Collections.emptyList();
    }
    
    public <T> List<T> getList(Set<String> keys, Class<T> tClass, boolean autoType) {
    	List<String> list = getList(keys);
    	List<T> tList = new ArrayList<>();
    	for (String value : list) {
    		T obj;
        	if (autoType) {
        		obj = JSON.parseObject(value, tClass, autoTypeParserConfig);
    		} else {
    			obj = JSON.parseObject(value, tClass);
    		}
        	tList.add(obj);
    	}
    	return tList;
    }

	public Boolean exist(String key, boolean close) {
		try {
			return jedis.exists(key);
        } catch (Exception e) {
            LOGGER.error("", e);
        } finally {
        	if (close) {
        		jedis.close();
			}
        }
		return false;
	}
	
	public void close() {
		jedis.close();
	}
	
	public Jedis getJedis() {
		return jedis;
	}
}
