//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.redis;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.ZParams;

/**
 * @author phil.shen on 2018/3/19
 **/
@SuppressWarnings("unchecked")
public class RedisSortedSet {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RedisSortedSet.class);

    private Jedis jedis;
    private boolean autoClose = true;

    public RedisSortedSet(Jedis jedis) {
        this.jedis = jedis;
    }
    
    public void close() {
		jedis.close();
	}
    
    /**
	 * 需要进行多条命令操作时，调用此方法可以关闭自动资源释放，操作完成后需手动调用close()进行资源释放
	 * @return
	 */
	public RedisSortedSet multipleCommand() {
		autoClose = false;
		return this;
	}

	public Long add(String key, String member, double source) {
		Map<String, Double> sourceMembers = new HashMap<>();
		sourceMembers.put(member, source);
		return add(key, sourceMembers);
	}
	
    /**
     * 向有序集合添加一个或多个成员，或者更新已存在成员的分数
     * @param key
     * @param sourceMembers
     * @return
     */
    public Long add(String key, Map<String, Double> sourceMembers) {
    	try {
    		return jedis.zadd(key, sourceMembers);
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
     * 获取有序集合的成员数
     * @param key
     * @return
     */
    public Long card(String key) {
    	try {
    		return jedis.zcard(key);
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
     * 计算在有序集合中指定区间分数的成员数
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Long count(String key, double min, double max) {
    	try {
    		return jedis.zcount(key, min, max);
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
     * 有序集合中对指定成员的分数加上增量 increment
     * @param key
     * @param member
     * @param increment
     * @return
     */
    public Double incrBy(String key, String member, double increment) {
    	try {
    		return jedis.zincrby(key, increment, member);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if (autoClose) {
				close();
			}
		}
    	return -1D;
    }

    /**
     * 计算给定的一个或多个有序集的交集并将结果集存储在新的有序集合 key 中
     * @param dstkey
     * @param sets
     * @return
     */
    public Long zinterstore(String dstkey, String... sets) {
    	try {
    		return jedis.zinterstore(dstkey, sets);
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
     * 计算给定的一个或多个有序集的交集并将结果集存储在新的有序集合 key 中
     * @param dstkey
     * @param params
     * @param sets
     * @return
     */
    public Long zinterstore(String dstkey, ZParams params, String... sets) {
    	try {
    		return jedis.zinterstore(dstkey, params, sets);
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
     * 在有序集合中计算指定字典区间内成员数量
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Long lexCount(String key, String min, String max) {
    	try {
    		return jedis.zlexcount(key, min, max);
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
     * 通过索引区间返回有序集合成指定区间内的成员
     * @param key
     * @param start
     * @param stop
     * @return
     */
	public Set<String> range(String key, long start, long stop) {
    	try {
    		return jedis.zrange(key, start, stop);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if (autoClose) {
				close();
			}
		}
    	return Collections.EMPTY_SET;
    }
	
	public Set<Tuple> rangeWithScores(String key, long start, long stop) {
		try {
    		return jedis.zrangeWithScores(key, start, stop);
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
     * 通过字典区间返回有序集合的成员
     * @param key
     * @param min
     * @param max
     * @return
     */
	public Set<String> rangeByLex(String key, String min, String max) {
    	try {
    		return jedis.zrangeByLex(key, min, max);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if (autoClose) {
				close();
			}
		}
    	return Collections.EMPTY_SET;
    }

    public Set<String> zrangeByLex(String key, String min, String max, int offset, int count) {
    	try {
    		return jedis.zrangeByLex(key, min, max, offset, count);
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
     * 通过分数返回有序集合指定区间内的成员
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Set<String> rangeByScore(String key, double min, double max) {
    	try {
    		return jedis.zrangeByScore(key, String.valueOf(min), String.valueOf(max));
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
     * 返回有序集合中指定成员的索引
     * @param key
     * @param member
     * @return
     */
    public Long rank(String key, String member) {
    	try {
    		return jedis.zrank(key, member);
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
     * 移除有序集合中的一个或多个成员
     * @param key
     * @param members
     * @return
     */
    public Long rem(String key, String... members) {
    	try {
    		return jedis.zrem(key, members);
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
     * 移除有序集合中给定的字典区间的所有成员
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Long remRangeByLex(String key, String min, String max) {
    	try {
    		return jedis.zremrangeByLex(key, min, max);
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
     * 移除有序集合中给定的排名区间的所有成员
     * @param key
     * @param start
     * @param stop
     * @return
     */
    public Long remRangeByRank(String key, long start, long stop) {
    	try {
    		return jedis.zremrangeByRank(key, start, stop);
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
     * 移除有序集合中给定的分数区间的所有成员
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Long remRangeByScore(String key, String min, String max) {
    	try {
    		return jedis.zremrangeByScore(key, min, max);
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
     * 返回有序集中指定区间内的成员，通过索引，分数从高到底
     * @param key
     * @param start
     * @param stop
     * @return
     */
    public Set<String> revrange(String key, long start, long stop) {
    	try {
    		return jedis.zrevrange(key, start, stop);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if (autoClose) {
				close();
			}
		}
    	return Collections.EMPTY_SET;
    }
    
    public Set<Tuple> revrangeWithScores(String key, long start, long stop) {
    	try {
    		return jedis.zrevrangeWithScores(key, start, stop);
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
     * 返回有序集中指定分数区间内的成员，分数从高到低排序
     * @param key
     * @param max
     * @param min
     * @return
     */
    public Set<String> revrangeByScore(String key, String max, String min) {
    	try {
    		return jedis.zrevrangeByScore(key, max, min);
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
     * 返回有序集合中指定成员的排名，有序集成员按分数值递减(从大到小)排序
     * @param key
     * @param member
     * @return
     */
    public Long revrank(String key, String member) {
    	try {
    		return jedis.zrevrank(key, member);
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
     * 返回有序集中，成员的分数值
     * @param key
     * @param member
     * @return
     */
    public Double score(String key, String member) {
    	try {
    		return jedis.zscore(key, member);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if (autoClose) {
				close();
			}
		}
    	return -1D;
    }

    /**
     * 计算给定的一个或多个有序集的并集，并存储在新的 key 中
     * @param dstkey
     * @param sets
     * @return
     */
    public Long unionstore(String dstkey, String... sets) {
    	try {
    		return jedis.zunionstore(dstkey, sets);
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
     * 计算给定的一个或多个有序集的并集，并存储在新的 key 中
     * @param dstkey
     * @param params
     * @param sets
     * @return
     */
    public Long unionstore(String dstkey, ZParams params, String... sets){
    	try {
    		return jedis.zunionstore(dstkey, params, sets);
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
     * 迭代有序集合中的元素（包括元素成员和元素分值）
     * @param key
     * @param cursor
     * @return
     */
    public ScanResult<Tuple> scan(String key, String cursor) {
    	try {
    		return jedis.zscan(key, cursor);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			if (autoClose) {
				close();
			}
		}
    	return null;
    }
}
