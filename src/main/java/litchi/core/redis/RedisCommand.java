//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.redis;

import redis.clients.jedis.Jedis;

public class RedisCommand {
	
	private Jedis jedis;

	public RedisCommand(Jedis jedis) {
		this.jedis = jedis;
	}

	public RedisKeyValue kv() {
    	RedisKeyValue kv = new RedisKeyValue(jedis);
    	return kv;
    }

    public RedisHash hash() {
        RedisHash hash = new RedisHash(jedis);
        return hash;
    }

    public RedisList list() {
        RedisList list = new RedisList(jedis);
        return list;
    }

    public RedisSet set() {
        RedisSet set = new RedisSet(jedis);
        return set;
    }

    public RedisSortedSet sortedSet() {
        RedisSortedSet sortedSet = new RedisSortedSet(jedis);
        return sortedSet;
    }
}
