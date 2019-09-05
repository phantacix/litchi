//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
//package litchi.core.dataconfig.parse;
//
//import org.dom4j.Attribute;
//import org.dom4j.Document;
//import org.dom4j.Element;
//import org.dom4j.io.SAXReader;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import litchi.core.common.utils.StringUtils;
//import litchi.core.dataconfig.ConfigAdapter;
//import litchi.core.dataconfig.annotation.FieldName;
//import litchi.core.dataconfig.annotation.IndexPK;
//
//import java.io.ByteArrayInputStream;
//import java.lang.reflect.Field;
//import java.lang.reflect.Modifier;
//import java.util.*;
//
///**
// * xml格式数据解析
// *
// * @author 0x737263
// */
//public class XMLDataParser implements DataParser {
//	protected static final Logger LOGGER = LoggerFactory.getLogger(XMLDataParser.class);
//
//	private static String FILE_EXT_NAME = ".xml";
//
//	@Override
//	public <T extends ConfigAdapter> List<T> parse(String text, Class<T> clazz) {
//		List<T> objList = new ArrayList<>();
//		SAXReader saxReader = new SAXReader();
//		saxReader.setEncoding("UTF-8");
//		Document document;
//		try {
//			Map<String, Field> fieldList = getFieldList(clazz);
//			document = saxReader.read(new ByteArrayInputStream(text.getBytes()));
//			Element rootElement = document.getRootElement();
//			List<?> elementList = rootElement.elements();
//
//			// 检查nameList是否都存在.不存在则提示error.
//			checkField(clazz.getName(), fieldList, elementList);
//
//			for (int i = 0; i < elementList.size(); i++) {
//				Element subElement = (Element) elementList.get(i);
//				List<?> attributeList = subElement.attributes();
//
//				T object = newInstance(clazz);
//				if (object == null) {
//					LOGGER.error("data data [{}] file new instance error!", clazz.getName());
//					return null;
//				}
//
//				for (int j = 0; j < attributeList.size(); j++) {
//					Attribute attribute = (Attribute) attributeList.get(j);
//					if (StringUtils.isBlank(attribute.getText())) {
//						continue;// 如果是空值则保留类声明里面的默认值
//					}
//
//					Field field = fieldList.get(attribute.getName());
//					if (field == null) {
//						LOGGER.warn("[{}]->[{}] column not exists in class!", clazz.getName(), attribute.getName());
//						continue;
//					}
//
//					Class<?> typeClass = field.getType();
//					if (typeClass.getCanonicalName() == int.class.getCanonicalName()) {
//						field.set(object, Integer.valueOf(attribute.getText()));
//					} else if (typeClass.getCanonicalName() == long.class.getCanonicalName()) {
//						field.set(object, Long.valueOf(attribute.getText()));
//					} else if (typeClass.getCanonicalName() == float.class.getCanonicalName()) {
//						field.set(object, Float.valueOf(attribute.getText()));
//					} else if (typeClass.getCanonicalName() == boolean.class.getCanonicalName()) {
//						field.set(object, Boolean.valueOf(attribute.getText()));
//					} else if (typeClass.getCanonicalName() == short.class.getCanonicalName()) {
//						field.set(object, Short.valueOf(attribute.getText()));
//					} else if (typeClass.getCanonicalName() == byte.class.getCanonicalName()) {
//						field.set(object, Byte.valueOf(attribute.getText()));
//					} else if (typeClass.getCanonicalName() == double.class.getCanonicalName()) {
//						field.set(object, Double.valueOf(attribute.getText()));
//					} else {
//						field.set(object, String.valueOf(attribute.getText()));
//					}
//				}
//				objList.add(object);
//			}
//		} catch (Exception e) {
//			LOGGER.error(String.format("loading [%s] class error!", clazz.getName()), e);
//		}
//
//		return objList;
//	}
//
//	@Override
//	public String fileExtensionName() {
//		return FILE_EXT_NAME;
//	}
//
//	protected Map<String, Field> getFieldList(Class<?> clazz) {
//		Map<String, Field> fieldList = new HashMap<>();
//		Field[] fields = clazz.getDeclaredFields();
//		for (Field f : fields) {
//			f.setAccessible(true);
//
//			// 忽略静态Field
//			if (Modifier.isStatic(f.getModifiers())) {
//				continue;
//			}
//
//			// 添加有@IndexPk的字段
//			if (f.isAnnotationPresent(IndexPK.class)) {
//				fieldList.put(f.getName(), f);
//				continue;
//			}
//
//			// 添加有@FiledName的字段
//			if (f.isAnnotationPresent(FieldName.class)) {
//				FieldName annoFieldName = f.getAnnotation(FieldName.class);
//				if (StringUtils.isBlank(annoFieldName.newName())) {
//					fieldList.put(f.getName(), f);
//				} else {
//					fieldList.put(annoFieldName.newName(), f);
//				}
//			}
//		}
//		return fieldList;
//	}
//
//	private void checkField(String className, Map<String, Field> fieldList, List<?> elementList) {
//		if (elementList.size() < 1) {
//			LOGGER.error("data data [{}] file 0 row record!", className);
//			return;
//		}
//
//		Element subElement = (Element) elementList.get(0);
//		List<?> attributeList = subElement.attributes();
//
//		Set<String> attributeNameList = new HashSet<>();
//		for (int j = 0; j < attributeList.size(); j++) {
//			Attribute attribute = (Attribute) attributeList.get(j);
//			attributeNameList.add(attribute.getName());
//		}
//
//		for (String fieldName : fieldList.keySet()) {
//			if (attributeNameList.contains(fieldName) == false) {
//				LOGGER.warn("class [{}]->[{}] not exists in data data file.", className, fieldName);
//			}
//		}
//	}
//
//	private <T> T newInstance(Class<T> cls) {
//		try {
//			return cls.newInstance();
//		} catch (InstantiationException e) {
//			LOGGER.error("", e);
//		} catch (IllegalAccessException e) {
//			LOGGER.error("", e);
//		}
//		return null;
//	}
//
//	@Override
//	public String format(String text) {
//		return text;
//	}
//}
