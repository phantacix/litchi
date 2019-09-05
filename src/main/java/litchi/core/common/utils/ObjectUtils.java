//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * 序列化对象转换类
 * @author 0x737263
 *
 */
public class ObjectUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(ObjectUtils.class);

	/**
	 * 对象转byte[]
	 * @param obj	对象
	 * @return
	 */
	public static byte[] object2ByteArray(Object obj) {
		if (obj == null) {
			return null;
		}
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			new ObjectOutputStream(bos).writeObject(obj);

			return bos.toByteArray();
		} catch (IOException ex) {
			LOGGER.error("failed to serialize obj.", ex);
		}
		return null;
	}

	/**
	 * byte[]转 对象
	 * @param buffer
	 * @return
	 */
	public static Object byteArray2Object(byte[] buffer) {
		if ((buffer == null) || (buffer.length == 0)) {
			return null;
		}

		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception ex) {
			LOGGER.error("failed to deserialize obj", ex);
			return null;
		} finally {
			try {
				if (ois != null)
					ois.close();
			} catch (Exception ex) {
			}
			try {
				bais.close();
			} catch (Exception ex) {
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T valueOf(String value, T defaultValue) {
		try {
			if (defaultValue.getClass() == Integer.class) {
				return (T) Integer.valueOf(value);
			} else if (defaultValue.getClass() == Long.class) {
				return (T) Long.valueOf(value);
			} else if (defaultValue.getClass() == Short.class) {
				return (T) Short.valueOf(value);
			} else if (defaultValue.getClass() == Byte.class) {
				return (T) Byte.valueOf(value);
			} else if (defaultValue.getClass() == Boolean.class) {
				if ("true".equals(value.toLowerCase()) || "false".equals(value.toLowerCase())) {
					return (T) Boolean.valueOf(value);
				}
				return (T) NumberUtils.intToBoolean(value);
			} else if (defaultValue.getClass() == String.class) {
				return (T) String.valueOf(value);
			}
		} catch (Exception ex) {

		}
		return defaultValue;
	}
}