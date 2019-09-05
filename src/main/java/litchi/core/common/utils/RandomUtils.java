//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.common.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 随机工具类
 *
 * @author 0x737263
 */
public class RandomUtils {
	
	private static Logger LOGGER = LoggerFactory.getLogger(RandomUtils.class);

    public static ThreadLocalRandom rand() {
        return ThreadLocalRandom.current();
    }

    /**
     * 随机随机范围的索引
     *
     * @param size 大小
     * @return
     */
    public static int nextIntIndex(int size) {
        return nextInt(0, size - 1);
    }

    /**
     * 随机范围值 minValue到 maxValue的闭区间
     *
     * @param minValue
     * @param maxValue
     * @return
     */
    public static int nextInt(int minValue, int maxValue) {
        if (minValue == maxValue) {
            return minValue;
        }

        if (maxValue - minValue <= 0) {
            return minValue;
        }
        return minValue + rand().nextInt(maxValue - minValue + 1);
    }

    /**
     * 是否命中
     *
     * @param rate     概率
     * @param maxValue 最大值
     * @return
     */
    public static boolean isHit(int rate, int maxValue) {
        if (rate < 1) {
            return false;
        }

        if (rate == maxValue) {
            return true;
        }

        return nextInt(1, maxValue) <= rate;
    }
    
    public static boolean isHit(int rate, int maxValue, boolean show) {
    	if (rate < 1) {
    		return false;
    	}
    	
    	if (rate == maxValue) {
    		return true;
    	}
    	int nextInt = nextInt(1, maxValue);
    	if (show) {
    		LOGGER.info("random value : {}", nextInt);
		}
    	return nextInt <= rate;
    }

    /**
     * 根据概率配置,返回随机命中的ID
     * 比如:
     * ID1_500|ID2_500
     * 每个id都有50%概率出现,通过randomHit返回随机命中的ID
     *
     * @param base 概率的最大值
     * @param map  ID和对应的出现概率
     * @return
     */
    public static <ID> ID randomHit(int base, Map<ID, Integer> map) {
        int rate = nextInt(1, base);
        int total = 0;
        for (Entry<ID, Integer> entry : map.entrySet()) {
            total += entry.getValue();
            if (total >= rate) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static <ID> ID randomHit(Collection<ID> collection) {
        if (collection == null || collection.size() < 1) {
            return null;
        }

        Iterator<ID> it = collection.iterator();
        int i = 0;
        int randomNum = nextIntIndex(collection.size());
        while (it.hasNext()) {
        	ID next = it.next();
            if (i == randomNum) {
                return next;
            }
            i++;
        }

        return null;
    }

}