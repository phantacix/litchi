//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.common.utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 集合工具类
 * @author 0x737263
 *
 */
public abstract class CollectionUtils {
	
	/**
	 * 连续复制一段集合对象
	 * @param source	数据源集合
	 * @param start		开始索引
	 * @param count		复制总记录数
	 * @return
	 */
	public static <T> List<T> subListCopy(List<T> source, int start, int count) {
		if ((source == null) || (source.size() == 0)) {
			return new ArrayList<T>(0);
		}

		int fromIndex = (start <= 0) ? 0 : start;
		if (start > source.size()) {
			fromIndex = source.size();
		}

		count = (count <= 0) ? 0 : count;
		int endIndex = fromIndex + count;
		if (endIndex > source.size()) {
			endIndex = source.size();
		}
		return new ArrayList<T>(source.subList(fromIndex, endIndex));
	}
	
	@SafeVarargs
	public static <T> List<T> asList(T ...items) {
		List<T> list = new ArrayList<T>();
		if (items != null) {
			for (T item : items) {
				list.add(item);
			}
		}
		return list;
	}
	
	public static <T,S> Map<T,S> asMap(T key, S val) {
		Map<T, S> map = new HashMap<T, S>();
		map.put(key, val);
		return map;
	}
	
	/**
	 * 截取分页集合
	 * @param list			集合
	 * @param startIndex	索引
	 * @param fetchCount	获取总数
	 * @return
	 */
	public static <T> List<T> pageResult(List<T> list, int startIndex, int fetchCount) {
		if ((list != null) && (list.size() > 0)) {
			if (startIndex >= list.size()) {
				return null;
			}
			startIndex = (startIndex < 0) ? 0 : startIndex;
			if (fetchCount <= 0) {
				return list.subList(startIndex, list.size());
			}
			int toIndex = Math.min(startIndex + fetchCount, list.size());
			return list.subList(startIndex, toIndex);
		}

		return null;
	}
	
	/**
	 * 判断集合是否有元素
	 * @param c
	 * @return
	 */
	public static <T> boolean isEmpty(Collection<T> c) {
		return c == null || c.size() == 0;
	}
	
	/**
	 * 判断集合是否有元素
	 * @param c
	 * @return
	 */
	public static <T> boolean isNotEmpty(Collection<T> c) {
		return c != null && c.size() > 0;
	}
	
	/**
	 * 判断字典是否有元素
	 * @param c
	 * @return
	 */
	public static <K,V> boolean isEmpty(Map<K,V> c) {
		return c == null || c.size() == 0;
	}
	
	/**
	 * 判断字典是否有元素
	 * @param c
	 * @return
	 */
	public static <K,V> boolean isNotEmpty(Map<K,V> c) {
		return c != null && c.size() > 0;
	}
	
	/**
	 * 将collection2中的元素从collection1中移除
	 * @param collection1
	 * @param collection2
	 */
	public static <T> void detainAll(Collection<T> collection1, Collection<T> collection2) {
		if (isEmpty(collection1) || isEmpty(collection2)) {
			return;
		}
		
		Set<T> set = new HashSet<T>();
		set.addAll(collection2);
		Iterator<T> iter = collection1.iterator();
		while(iter.hasNext()) {
			if (set.contains(iter.next())) {
				iter.remove();
			}
		}
	}
	
	public static <K,V> void detainAll(Map<K, V> map, Set<K> exclutions) {
		if (isEmpty(exclutions) || isEmpty(exclutions)) {
			return;
		}
		
		for (K key : exclutions) {
			map.remove(key);
		}
	}
	
	/**
	 * 判断两个集合的内容是否是一样的(顺序不一定相同)
	 * @param collection1
	 * @param collection2
	 * @return
	 */
	public static <T> boolean isSame(Collection<T> collection1, Collection<T> collection2) {
		Set<T> set1 = new HashSet<>();
		Set<T> set2 = new HashSet<>();
		set1.addAll(collection1);
		set2.addAll(collection2);
		
		for (T t : collection1) {
			if (!set2.contains(t)) {
				return  false;
			}
		}
		
		for (T t : collection2) {
			if (!set1.contains(t)) {
				return false;
			}
		}
		return true;
	}
	
	public static <T> boolean isNotSame(Collection<T> collection1, Collection<T> collection2) {
		return isSame(collection1, collection2) == false;
	}
	
	
	//===========dx
	/**
	 * 获取集合页数
	 * @param list
	 * @param pageSement
	 * @return
	 */
	public static <T> int findPages(List<T> list, int pageSement) {
		int pages = list.size() / pageSement;
		return list.size() % pageSement == 0 ? pages : pages + 1;
	}

	/**
	 * 获取第X页
	 * @param list
	 * @param page
	 * @return
	 */
	public static <T> Collection<T> getPages(List<T> list, int page, int pageSement) {
		Collection<T> pages = new ArrayList<>();
		Page p = Page.valueOf(page, pageSement);
		int start = (int) (p.getStart() - 1);
		int end = (int) (p.getEnd());
		int indexLimit = list.size() - 1;
		for (int i = start; i < end; i++) {
			if (i > indexLimit) {
				break;
			}
			T t = list.get(i);
			if (t == null) {
				break;
			} else {
				pages.add(t);
			}
		}
		return pages;
	}
	
	public static class Page {
		private int page;

		private long start;

		private long end;

		public Page(int page, long start, long end) {
			this.page = page;
			this.start = start;
			this.end = end;
		}

		public boolean inPage(long value) {
			return value >= start && page <= end;
		}

		public static int findPage(long value, int segment) {
			if (value % segment == 0) {
				return (int) (value / segment);
			}
			return (int) (value / segment + 1);
		}

		private static final int DEFAULT_SEGMENT = 50;

		private static ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Page>> cache = new ConcurrentHashMap<>();

		static {
			ConcurrentHashMap<Integer, Page> pages = new ConcurrentHashMap<>();
			for (int i = 1; i <= 10; i++) {
				int start = (i - 1) * DEFAULT_SEGMENT + 1;
				long end = i * DEFAULT_SEGMENT;
				pages.put(i, new Page(i, start, end));
			}
			cache.put(DEFAULT_SEGMENT, pages);
		}

		public static List<Page> findBetweenPages(long from, long to, int segment) {
			List<Page> pages = new ArrayList<>();
			int fromPage = findPage(from, segment);
			int toPage = findPage(to, segment);
			for (int i = 0, l = fromPage - toPage; i < l; i++) {
				pages.add(valueOf(fromPage - i, segment));
			}
			return pages;
		}

		public static Page valueOf(int page, int segment) {
			ConcurrentHashMap<Integer, Page> pages = cache.get(segment);
			if (pages == null) {
				pages = new ConcurrentHashMap<>();
				cache.put(segment, pages);
			}
			Page p = pages.get(page);
			if (p == null) {
				int start = (page - 1) * segment + 1;
				long end = page * segment;
				p = new Page(page, start, end);
				pages.put(page, p);
			}
			return p;
		}

		public int getPage() {
			return page;
		}

		public void setPage(int page) {
			this.page = page;
		}

		public long getStart() {
			return start;
		}

		public void setStart(long start) {
			this.start = start;
		}

		public long getEnd() {
			return end;
		}

		public void setEnd(long end) {
			this.end = end;
		}
	}
	
	public static <T> List<T> random(List<T> collection, int num) {
    	if (collection.size() == 0) {
    		return null;
    	}
    	List<T> list = new ArrayList<>();
    	if (collection.isEmpty()) {
			return list;
		}
    	if (num >= collection.size()) {
    		list.addAll(collection);
			return list;
		}
    	for (int i = 0; i < 1000; i++) {
    		int r = ThreadLocalRandom.current().nextInt(0, collection.size());
    		T obj = collection.get(r);
    		if (list.contains(obj)) {
				continue;
			}
			list.add(obj);
			num--;
			if (num <= 0) {
				break;
			}
		}
        return list;
    }
	
	public static <T> T random(Collection<T> collection) {
    	if (collection.size() == 0) {
    		return null;
    	}
    	int r = ThreadLocalRandom.current().nextInt(0, collection.size());
    	int index = 0;
    	for (T t : collection) {
			if (index == r) {
				return t;
			}
			index++;
		}
    	return null;
    }
}