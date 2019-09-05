//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.redis;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import litchi.core.Constants;
import litchi.core.Litchi;
import litchi.core.common.utils.StringUtils;
import litchi.core.components.Component;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;

/**
 * @author phil.shen on 2018/3/19
 **/
public class RedisComponent implements Component {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisComponent.class);

    private String defaultRedisId;
    
    private final Map<String, JedisPoolPack> jedisPoolMap = new HashMap<>();
    private final List<JedisPoolPack> sharedJedisPool = new ArrayList<>();

    public RedisComponent(Litchi litchi) {
        JSONArray redisIds = litchi.currentNode().getJsonArrayOpts("redisIds");
        Map<String, Integer> connectRedisIds = new HashMap<>();
        for (int i = 0; i < redisIds.size(); i++) {
            JSONObject item = redisIds.getJSONObject(i);
            connectRedisIds.put(item.getString("id"), item.getInteger("dbIndex"));
        }

        JSONArray redisArray = litchi.config().getJSONArray(Constants.Component.REDIS);
        List<RedisConfig> configList = new ArrayList<>();

        for (int i = 0; i < redisArray.size(); i++) {
            JSONObject jsonObject = redisArray.getJSONObject(i);

            String redisId = jsonObject.getString("id");
            if (connectRedisIds.containsKey(redisId) == false) {
                continue;
            }
            int dbIndex = connectRedisIds.get(redisId);
            configList.add(new RedisConfig(jsonObject, dbIndex));
            if (StringUtils.isBlank(defaultRedisId)) {
                defaultRedisId = redisId;
            }
        }

        init(defaultRedisId, configList);
    }

    public RedisComponent(JSONArray redisArray, List<JSONObject> connectRedis) {
    	Map<String, Integer> connectRedisIds = new HashMap<>();
    	for (JSONObject serverRedis : connectRedis) {
    		connectRedisIds.put(serverRedis.getString("id"), serverRedis.getInteger("dbIndex"));
    	}
        List<RedisConfig> configList = new ArrayList<>();
        for (Object redis : redisArray) {
            JSONObject jsonObject = (JSONObject) redis;
            String redisId = jsonObject.getString("id");
            if (connectRedisIds.containsKey(redisId) == false) {
				continue;
			}
            int dbIndex = connectRedisIds.get(redisId); 
            
            configList.add(new RedisConfig(jsonObject, dbIndex));
            if (StringUtils.isBlank(defaultRedisId)) {
            	defaultRedisId = redisId;
			}
        }
        init(defaultRedisId, configList);
    }
    
    public RedisComponent(String defaultRedisId, Collection<RedisConfig> redisConfigs) {
    	init(defaultRedisId, redisConfigs);
    }

    private void init(String defaultRedisId, Collection<RedisConfig> redisConfigs) {
        if (StringUtils.isBlank(defaultRedisId)) {
            throw new RedisConfigErrorException("default Redis Id could not be empty.");
        }
        this.defaultRedisId = defaultRedisId;
        for (RedisConfig redisConfig : redisConfigs) {
            try {
                if (StringUtils.isBlank(redisConfig.getKey())) {
                    throw new RedisConfigErrorException("default Redis Id could not be empty.");
                }
                if (jedisPoolMap.containsKey(redisConfig.getKey())) {
                    throw new RedisConfigErrorException("Redis Id is multiple. redisId=" + redisConfig.getKey());
                }
                GenericObjectPoolConfig config = new GenericObjectPoolConfig();
                if (redisConfig.getMaxConnect() > 0) {
                	config.setMaxTotal(redisConfig.getMaxConnect());
				}
                if (redisConfig.getMaxIdleConnect() > 0) {
                	config.setMinIdle(redisConfig.getMaxIdleConnect());
				}
                if (redisConfig.getMinIdleConnect() > 0) {
                	config.setMaxIdle(redisConfig.getMinIdleConnect());
				}
                JedisPool jedisPool;
                if (redisConfig.hasPassword()) {
                    jedisPool = new JedisPool(config, redisConfig.getHost(), redisConfig.getPort(), 1000, redisConfig.getPassword());
                } else {
                    jedisPool = new JedisPool(config, redisConfig.getHost(), redisConfig.getPort(), 1000);
                }
                JedisPoolPack jedisPoolPack = new JedisPoolPack(jedisPool, redisConfig);
                jedisPoolMap.put(redisConfig.getKey(), jedisPoolPack);
                
                if (redisConfig.isShared()) {
                	sharedJedisPool.add(jedisPoolPack);
				}
                LOGGER.info("jedis pool initialize, id={} addr: {}, port: {}", redisConfig.getKey(), redisConfig.getHost(), redisConfig.getPort());
            } catch (Exception e) {
                LOGGER.error("", e);
                throw new RedisConfigErrorException();
            }
        }
        if (jedisPoolMap.containsKey(defaultRedisId) == false) {
            LOGGER.error("default Redis Id not exist. defaultRedisId={}", defaultRedisId);
            throw new RedisConfigErrorException();
        }
    }

    private Jedis getJedis(String redisKey) {
        try {
        	JedisPoolPack jedisPoolPack = jedisPoolMap.get(redisKey);
            JedisPool jedisPool = jedisPoolPack.getJedisPool();
            if (jedisPool == null) {
                LOGGER.error("jedis pool not found. key={} existKeys={}", redisKey, jedisPoolMap.keySet());
                return null;
            }
            Jedis jedis = jedisPool.getResource();
            jedis.select(jedisPoolPack.getDbIndex());
            return jedis;
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return null;
    }

    public Jedis getJedis() {
        return getJedis(defaultRedisId);
    }
    
    public RedisKeyValue kv(String redisId) {
    	Jedis jedis = getJedis(redisId);
    	RedisKeyValue kv = new RedisKeyValue(jedis);
    	return kv;
    }
    
    public RedisKeyValue kv() {
    	return kv(defaultRedisId);
    }

    public RedisHash hash() {
        Jedis jedis = getJedis();
        RedisHash hash = new RedisHash(jedis);
        return hash;
    }

    public RedisList list() {
        Jedis jedis = getJedis();
        RedisList list = new RedisList(jedis);
        return list;
    }

    public RedisSet set() {
        Jedis jedis = getJedis();
        RedisSet set = new RedisSet(jedis);
        return set;
    }

    public RedisSortedSet sortedSet() {
        Jedis jedis = getJedis();
        RedisSortedSet sortedSet = new RedisSortedSet(jedis);
        return sortedSet;
    }

    @Override
    public String name() {
        return Constants.Component.REDIS;
    }

    @Override
    public void start() {
        for (Map.Entry<String, JedisPoolPack> entry : jedisPoolMap.entrySet()) {
        	Jedis jedis = null;
        	try {
        		LOGGER.debug("redis key:{} ping test.", entry.getKey());
        		jedis = entry.getValue().getJedisPool().getResource();
        		LOGGER.debug("ping ...");
        		LOGGER.debug("redis key:{} ping result:{}", entry.getKey(), jedis.ping());
			} catch (Exception e) {
				LOGGER.error("", e);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
        }
    }

    @Override
    public void afterStart() {

    }

    @Override
    public void stop() {

    }

	public RedisCommand shared(long id) {
		if (sharedJedisPool.isEmpty()) {
			LOGGER.warn("shared redis is empty. please config in litchi.");
			return null;
		}
		int mod = (int) (id % sharedJedisPool.size());
		JedisPoolPack jedisPoolPack = sharedJedisPool.get(mod);
		Jedis jedis = jedisPoolPack.getJedis();
		return new RedisCommand(jedis);
	}
}
