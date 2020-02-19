package litchi.core.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xerial.snappy.Snappy;

/**
 * google snappy
 * @author 0x737263
 *
 */
public class SnappyHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(SnappyHelper.class);

	/**
	 * 压缩
	 * @param bytes
	 * @return
	 */
	public static byte[] compress(byte[] bytes) {
		try {
			return Snappy.compress(bytes);
		} catch (Exception e) {
			LOGGER.warn("{}", e);
		}
		return null;
	}
	
	/**
	 * 解压
	 * @param bytes
	 * @return
	 */
	public static byte[] uncompress(byte[] bytes) {
		try {
			return Snappy.uncompress(bytes);
		} catch (Exception e) {
			LOGGER.warn("{}", e);
		}
		return null;
	}
}
