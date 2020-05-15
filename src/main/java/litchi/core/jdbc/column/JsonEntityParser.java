package litchi.core.jdbc.column;

import java.lang.reflect.Constructor;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import litchi.core.jdbc.table.JsonEntity;

public class JsonEntityParser {

	private static Set<Class<?>> CHECKED_ENTITY_CLASSES = new HashSet<>();

	private static Charset CHARSET = Charset.forName("UTF-8");

	private static Logger LOGGER = LoggerFactory.getLogger(JsonEntityParser.class);

	public static Object parseJson(String value, Class<?> valueType) {
		// 检查JsonEntity实现类是否存在空构造方法
		if (valueType == JsonEntity.class && !CHECKED_ENTITY_CLASSES.contains(valueType)) {
			synchronized (CHECKED_ENTITY_CLASSES) {
				Constructor<?>[] constructors = valueType.getConstructors();
				boolean hasEmptyConstructor = false;
				for (Constructor<?> constructor : constructors) {
					if (constructor.getParameterCount() == 0) {
						hasEmptyConstructor = true;
						break;
					}
				}
				if (hasEmptyConstructor) {
					CHECKED_ENTITY_CLASSES.add(valueType);
				} else {
					LOGGER.error("JsonEntity's subclass[{}] must has empty constructor!", valueType.getSimpleName());
				}
			}
		}
		return JSON.parseObject(value, valueType);
	}

	public static Object parseJson(byte[] byteJson, Class<?> valueType) {
		return parseJson(new String(byteJson, CHARSET), valueType);
	}

	public static List<?> parseArray(String value, Class<?> valueType) {
		// 检查JsonEntity实现类是否存在空构造方法
		if (valueType == JsonEntity.class && !CHECKED_ENTITY_CLASSES.contains(valueType)) {
			synchronized (CHECKED_ENTITY_CLASSES) {
				Constructor<?>[] constructors = valueType.getConstructors();
				boolean hasEmptyConstructor = false;
				for (Constructor<?> constructor : constructors) {
					if (constructor.getParameterCount() == 0) {
						hasEmptyConstructor = true;
						break;
					}
				}
				if (hasEmptyConstructor) {
					CHECKED_ENTITY_CLASSES.add(valueType);
				} else {
					LOGGER.error("JsonEntity's subclass[{}] must has empty constructor!", valueType.getSimpleName());
				}
			}
		}
		return JSON.parseArray(value, valueType);
	}

	public static List<?> parseArray(byte[] jsonBytes, Class<?> valueType) {
		return parseArray(new String(jsonBytes, CHARSET),  valueType);
	}
}
