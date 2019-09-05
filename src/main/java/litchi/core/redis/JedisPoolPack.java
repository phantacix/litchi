//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisPoolPack {
	
	private RedisConfig redisConfig;
	
	private JedisPool jedisPool;
	
	private int dbIndex;

	public JedisPoolPack(JedisPool jedisPool, RedisConfig redisConfig) {
		this.jedisPool = jedisPool;
		this.dbIndex = redisConfig.getDbIndex();
		this.redisConfig = redisConfig;
	}
	
	public JedisPool getJedisPool() {
		return jedisPool;
	}
	
	public int getDbIndex() {
		return dbIndex;
	}
	
	public RedisConfig getRedisConfig() {
		return redisConfig;
	}

	public Jedis getJedis() {
		Jedis jedis = getJedisPool().getResource();
		jedis.select(dbIndex);
		return jedis;
	}
	
	public Jedis getJedis(int dbIndex) {
		Jedis jedis = getJedisPool().getResource();
		if (jedis.getDB() != dbIndex) {
			jedis.select(dbIndex);
		}
		return jedis;
	}
}
