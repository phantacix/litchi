//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 字符串工具类
 *
 * @author 0x737263
 */
public class StringUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(StringUtils.class);

	public static boolean isBlank(String str) {
		if (str == null || "".equals(str.trim())) {
			return true;
		}
		return false;
	}

	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}

	/**
	 * 截取字符的长度
	 * @param str		原字符
	 * @param length	要求长度(不足以str最长为准)
	 * @return
	 */
	public static String subString(String str, int length) {
		if (isBlank(str)) {
			return "";
		}
		return str.substring(0, Math.min(str.length(), length));
	}

	/**
	 * byte[] 转 String
	 *
	 * @param stream
	 * @return
	 */
	public static String inputStream2String(InputStream stream) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		String returnString = "";
		try {
			while ((length = stream.read(buffer)) != -1) {
				bos.write(buffer, 0, length);
			}
			bos.close();
			stream.close();

			returnString = bos.toString().trim();
		} catch (Exception ex) {
			LOGGER.error("{}", ex);
		}

		return returnString;
	}

	/**
	 * 数组转字符串
	 *
	 * @param arr
	 * @return
	 */
	public static String array2String(Object[] arr) {
		StringBuffer sb = new StringBuffer();
		for (Object object : arr) {
			sb.append(object.toString());
			sb.append(",");
		}
		if (sb.length() > 1) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	/**
	 * 填充指定数组到指定长度
	 *
	 * @param src     源数组
	 * @param len     指定长度
	 * @param content 填充内容
	 * @return 生成的新数组 如果长度小于源数组长度，返回源数组
	 */
	public static String[] fillStringArray(String[] src, int len, String content) {
		if (src == null || src.length >= len) {
			return src;
		}

		if (content == null) {
			content = "";
		}
		String[] data;
		data = new String[len];
		for (int i = 0; i < len; i++) {
			if (i >= src.length) {
				data[i] = content;
			} else {
				if (src[i].isEmpty()) {
					src[i] = content;
				}
				data[i] = src[i];
			}
		}
		return data;
	}

	public static String[] split(String src, String splitable) {
		if (isBlank(src) || isBlank(splitable)) {
			return null;
		}

		String[] items = src.split(splitable);
		String[] result = new String[items.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = String.valueOf(items[i]);
		}
		return result;
	}


	public static String format(String str, String... args) {
		StringBuilder builder = new StringBuilder(str);
		
		int index = builder.indexOf("{}");
		int i = 0;
		while (index > -1) {
			if (args.length < i + 1) {
				break;
			}
			String arg = args[i];
			builder.replace(index, index + 2, arg);
			
			i++;
			index = builder.indexOf("{}");
		}
		
		return builder.toString();
	}
	
	/**
	 * 用大括号"{}"为占位符，填充字符串
	 * @param str
	 * @param args
	 * @return
	 */
	public static String format(String str, Object... args) {
		StringBuilder builder = new StringBuilder(str);
		
		int index = builder.indexOf("{}");
		int i = 0;
		while (index > -1) {
			if (args.length < i + 1) {
				break;
			}
			Object arg = args[i];
			builder.replace(index, index + 2, arg.toString());
			
			i++;
			index = builder.indexOf("{}");
		}
		
		return builder.toString();
	}

	/**
	 * 控制台格式输出
	 * @param str
	 * @param args
	 */
	public static void print(String str, Object... args) {
		System.out.println(format(str, args));
	}
	
	public static void printErr(String str, Object... args) {
		System.err.println(format(str, args));
	}
	
	/**
	 * 首字母小写
	 * @param str
	 * @return
	 */
	public static String firstCharLowerCase(String str) {
		if (str == null || str.length() < 1) {
			return "";
		}
		char[] chars = new char[1];
		chars[0] = str.charAt(0);
		String temp = new String(chars);
		String temp2 = temp.toLowerCase();
		return str.replaceFirst(temp, temp2);
	}
	
	/**
	 * 首字母大写
	 * @param str
	 * @return
	 */
	public static String firstCharUpperCase(String str) {
		if (str == null || str.length() < 1) {
			return "";
		}
		char[] chars = new char[1];
		chars[0] = str.charAt(0);
		String temp = new String(chars);
		String temp2 = temp.toUpperCase();
		return str.replaceFirst(temp, temp2);
	}
	
	
	
	
	
	/**
	 * 集合转换成字符串
	 * @param collection
	 * @param splitString {@code Splitable}}
	 * @return
	 */
	public static String collection2SplitString(Collection<? extends Object> collection, String splitString) {
		StringBuilder builder = new StringBuilder();
		if (collection == null || collection.isEmpty()) {
			return builder.toString();
		}
		for(Object obj : collection) {
			if (obj!= null) {
				builder.append(obj.toString()).append(splitString);
			}
		}
		if(builder.length() > 0) {
			builder.deleteCharAt(builder.length() - 1);
		}
		
		return builder.toString();
	}
	
	/**
	 * 字符串转List<Integer>
	 * @param delimiterString
	 * @param split
	 * @return
	 */
	public static List<Integer> delimiterString2IntList(String delimiterString, String split) {
		if ((delimiterString == null) || (delimiterString.trim().length() == 0)) {
			return new ArrayList<>();
		}

		List<Integer> list = new ArrayList<>();
		String[] ss = split(delimiterString.trim(), split);
		if (ss != null && ss.length > 0) {
			for (String str : ss) {
				list.add(Integer.valueOf(str));
			}
		}
		return list;
	}

	public static List<Long> delimiterString2List(String delimiterString, String split) {
		if ((delimiterString == null) || (delimiterString.trim().length() == 0)) {
			return new ArrayList<>();
		}

		List<Long> list = new ArrayList<>();
		String[] ss = split(delimiterString.trim(), split);
		if (ss != null && ss.length > 0) {
			for (String str : ss) {
				list.add(Long.valueOf(str));
			}
		}
		return list;
	}

	public static String firstUppercase(String str) {
		char baseChar = str.charAt(0);
		char updatedChar = Character.toUpperCase(baseChar);
		if (baseChar == updatedChar) {
			return str;
		}
		char[] chars = str.toCharArray();
		chars[0] = updatedChar;
		return new String(chars, 0, chars.length);
	}
	
	public static String firstLowercase(String str) {
		char baseChar = str.charAt(0);
		char updatedChar = Character.toLowerCase(baseChar);
		if (baseChar == updatedChar) {
			return str;
		}
		char[] chars = str.toCharArray();
		chars[0] = updatedChar;
		return new String(chars, 0, chars.length);
	}

	/**
	 * 从源串的指定位置开始查找指定字符标签对的字符串
	 * @param src
	 * @param start
	 * @param tagStart
	 * @param tagEnd
	 * @return
	 */
	public static String findPaireTag(String src, int start, char tagStart, char tagEnd) {
		String substring = src.substring(start);
		char[] charArray = substring.toCharArray();
		StringBuilder builder = new StringBuilder();
		boolean begin = false;
		int deep = 0;
		for (char c : charArray) {
			if (begin) {
				if (c == tagStart) {
					deep++;
					builder.append(c);
					continue;
				}
				if (c == tagEnd) {
					if (deep > 0) {
						deep--;
						builder.append(c);
						continue;
					}
					begin = false;
					break;
				}
				builder.append(c);
				continue;
			}
			if (c == tagStart) {
				begin = true;
				continue;
			}
		}
		return builder.toString();
	}

	public static int isFront(String v1, String v2) {
		if (v1 == null || v2 == null) {
			return 0;
		}
		v1 = v1.toLowerCase();
		v2 = v2.toLowerCase();
		for (int i = 0; i < v1.length(); i++) {
			char c1 = v1.charAt(i);
			if (v2.length() <= i) {
				return 1;
			}
			char c2 = v2.charAt(i);
			if (c1 < c2) {
				return -1;
			} else if (c1 > c2) {
				return 1;
			}
		}
		return 1;
	}

	/**
	 * 格式化异常堆栈信息
	 * @param e
	 * @return
	 */
	public static String formatException(Exception e) {
		StringBuilder builder = new StringBuilder();
		builder.append(e.getClass().getName()).append(": ").append(e.getMessage()).append("\n");
		StackTraceElement[] stackTrace = e.getStackTrace();
		for (StackTraceElement stackTraceElement : stackTrace) {
			builder.append("\t").append("at ")
				.append(stackTraceElement.getClassName()).append(".")
				.append(stackTraceElement.getMethodName()).append("(")
				.append(stackTraceElement.getFileName()).append(":")
				.append(stackTraceElement.getLineNumber()).append(")")
				.append("\n");
		}
		return builder.toString();
	}
}
