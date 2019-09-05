//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.util.List;
import java.util.Set;

/**
 * @author phil.shen on 2018/3/19
 **/
public class RedisSet {

    private Jedis jedis;

    public RedisSet(Jedis jedis) {
        this.jedis = jedis;
    }

    /**
     * 向集合添加一个或多个成员
     * @param key
     * @param members
     * @return
     */
    public Long add(String key, String... members) {
        return jedis.sadd(key, members);
    }

    /**
     * 获取集合的成员数
     * @param key
     * @return
     */
    public Long card(String key) {
        return jedis.scard(key);
    }

    /**
     * 返回给定所有集合的差集
     * @param keys
     * @return
     */
    public Set<String> siff(String... keys) {
        return jedis.sdiff(keys);
    }

    /**
     * 返回给定所有集合的差集并存储在 destination 中
     * @param destination
     * @param keys
     * @return
     */
    public Long siffstore(String destination, String... keys) {
        return jedis.sdiffstore(destination, keys);
    }

    /**
     * 返回给定所有集合的交集
     * @param keys
     * @return
     */
    public Set<String> inter(String... keys) {
        return jedis.sinter(keys);
    }

    /**
     * 返回给定所有集合的交集并存储在 destination 中
     * @param destination
     * @param keys
     * @return
     */
    public Long interstore(String destination, String... keys) {
        return jedis.sinterstore(destination, keys);
    }

    /**
     * 判断 member 元素是否是集合 key 的成员
     * @param key
     * @param member
     * @return
     */
    public Boolean isMember(String key, String member) {
        return jedis.sismember(key, member);
    }

    /**
     * 返回集合中的所有成员
     * @param key
     * @return
     */
    public Set<String> members(String key) {
        return jedis.smembers(key);
    }

    /**
     * 将 member 元素从 source 集合移动到 destination 集合
     * @param source
     * @param destination
     * @param member
     * @return
     */
    public Long move(String source, String destination, String member) {
        return jedis.smove(source, destination, member);
    }

    /**
     * 移除并返回集合中的一个随机元素
     * @param key
     * @return
     */
    public String pop(String key) {
        return jedis.spop(key);
    }

    /**
     * 返回集合中一个或多个随机数
     * @param key
     * @return
     */
    public String randMember(String key) {
        return jedis.srandmember(key);
    }

    /**
     * 返回集合中一个或多个随机数
     * @param key
     * @param count
     * @return
     */
    public List<String> randMember(String key, int count) {
        return jedis.srandmember(key, count);
    }

    /**
     * 移除集合中一个或多个成员
     * @param key
     * @param members
     * @return
     */
    public Long rem(String key, String... members) {
        return jedis.srem(key, members);
    }

    /**
     * 返回所有给定集合的并集
     * @param keys
     * @return
     */
    public Set<String> union(String... keys) {
        return jedis.sunion(keys);
    }

    /**
     * 所有给定集合的并集存储在 destination 集合中
     * @param destination
     * @param keys
     * @return
     */
    public Long unionstore(String destination, String... keys) {
        return jedis.sunionstore(destination, keys);
    }

    /**
     * 迭代集合中的元素
     * @param key
     * @param cursor
     * @return
     */
    public ScanResult<String> scan(String key, String cursor) {
        return jedis.sscan(key, cursor);
    }

    /**
     * 迭代集合中的元素
     * @param key
     * @param cursor
     * @param scanParams
     * @return
     */
    public ScanResult<String> scan(String key, String cursor, ScanParams scanParams) {
        return jedis.sscan(key, cursor, scanParams);
    }
}
