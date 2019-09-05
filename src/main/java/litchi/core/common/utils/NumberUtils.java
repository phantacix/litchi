//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.common.utils;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author 0x737263
 *
 */
public class NumberUtils {

	private static Logger LOGGER = LoggerFactory.getLogger(NumberUtils.class);

	public static final int TRUE = 1;
	public static final int FALSE = 0;

	public static final int THOUSAND = 1000;
	public static final int HUNDRED = 100;
	
	private static Pattern NUMBER_PATTERN = Pattern.compile("^-?\\d+$");

	/**
	 * Number类型转换具体的数字类型
	 * @param resultType
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T valueOf(Class<T> resultType, Number value) {
		if (resultType == null) {
			String msg = value.getClass().getSimpleName() + " -> NULL";
			throw new NullPointerException(msg);
		}

		if ((resultType == Integer.TYPE) || (resultType == Integer.class))
			return (T) Integer.valueOf(value.intValue());
		if ((resultType == Double.TYPE) || (resultType == Double.class))
			return (T) Double.valueOf(value.doubleValue());
		if ((resultType == Boolean.TYPE) || (resultType == Boolean.class))
			return (T) Boolean.valueOf(value.doubleValue() > 0.0D);
		if ((resultType == Byte.TYPE) || (resultType == Byte.class))
			return (T) Byte.valueOf(value.byteValue());
		if ((resultType == Long.TYPE) || (resultType == Long.class))
			return (T) Long.valueOf(value.longValue());
		if ((resultType == Short.TYPE) || (resultType == Short.class))
			return (T) Short.valueOf(value.shortValue());
		if ((resultType == Float.TYPE) || (resultType == Float.class))
			return (T) Float.valueOf(value.floatValue());
		if (resultType == Number.class) {
			return (T) value;
		}
		String msg = value.getClass().getSimpleName() + " -> " + resultType.getSimpleName();
		throw new IllegalArgumentException(new ClassCastException(msg));
	}

	/**
	 * 格式化的字符串转数组
	 * @param str			格式化字符串
	 * @param separator		分隔符{@code Splitable}
	 * @param clazz			目标集合类类型
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] convertArray(String str, String separator, Class<T> clazz) {
		if ((str != null) && (str.trim().length() > 0)) {
			String[] vals = str.split(separator);
			try {
				return (T[]) covertArray(clazz, vals, 0, vals.length);
			} catch (Exception e) {
				LOGGER.error("", e);
			}
		}
		return null;
	}

	/**
	 * 
	 * @param clazz
	 * @param vals
	 * @param from
	 * @param to
	 * @return
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private static Object[] covertArray(Class<?> clazz, String vals[], int from, int to)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		int start;
		int end;
		if (from > to) {
			start = to;
			end = from;
		} else {
			start = from;
			end = to;
		}
		Object result[] = (Object[]) Array.newInstance(clazz, to - from);
		Method valueOfMethod = clazz.getMethod("valueOf", new Class[] { String.class });
		boolean accessible = valueOfMethod.isAccessible();
		valueOfMethod.setAccessible(true);
		if (valueOfMethod != null) {
			for (int i = start; i < end; i++) {
				Object val = valueOfMethod.invoke(clazz, new Object[] { vals[i] });
				result[i - start] = val;
			}

		}
		valueOfMethod.setAccessible(accessible);
		return result;
	}

	/**
	 *获取某个数值的百分比数值
	 * @param value
	 * @param percent
	 * @return
	 */
	public static Number getNumberPercentValue(Number value, int percent) {
		return Math.ceil(value.longValue() * percent / (float) 100);
	}

	/**
	 * 获取某个数值的百分比数值
	 * @param value
	 * @param percent
	 * @return
	 */
	public static int getPercentValue(int value, int percent) {
		return getNumberPercentValue(value, percent).intValue();
	}

	/**
	 * 获取某个数值的比率数值
	 * @param value
	 * @param percent
	 * @param base
	 * @return
	 */
	public static int getPercentValue(Number value, int percent, int base) {
		return (int) Math.ceil(value.longValue() * percent / (float) base);
	}
	
	public static long getPercentValueL(long value, int percent, int base) {
		return (long) Math.ceil(value * percent / (float) base);
	}
	
	public static double getPercentValueD(double value, int percent, int base) {
		return Math.ceil(value * percent / base);
	}

	/**
	 * 获取某个数值的百分比数值
	 * @param value
	 * @param percent
	 * @return
	 */
	public static long getPercentValue(long value, int percent) {
		return getNumberPercentValue(value, percent).longValue();
	}

	/**
	 * 获取数值的百分比
	 * @param value	要计算的值
	 * @param basevalue	要计算的基数
	 * @param ratio	比率
	 * @return
	 */
	public static int getValuePercent(int value, int basevalue, int ratio) {
		return (int) (new BigDecimal((float) value / (float) basevalue).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue() * ratio);
	}

	public static int getValuePercent(int value, int basevalue) {
		return (int) (new BigDecimal((float) value / (float) basevalue).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue() * 100);
	}
	
	public static int getValuePercent(long value, long basevalue) {
		return (int) (new BigDecimal((double) value / (double) basevalue).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue() * 100);
	}

	/**
	 * int转化为boolean
	 * @param value
	 * @return
	 */
	public static Boolean intToBoolean(int value) {
		return value == 0 ? false : true;
	}

	public static Boolean intToBoolean(String value) {
		return value.equals("0") ? false : true;
	}

	/**
	 * boolean转化为int
	 * @param value
	 * @return
	 */
	public static int booleanToInt(boolean value) {
		return value ? 1 : 0;
	}
	
	public static Boolean StringToBoolean(String value) {
		return value.equals("0") ? false : true;
	}
	
	/**
	 * 是否是数字
	 * @param value
	 * @return
	 */
	public static boolean isNumber(String value) {
		Matcher matcher = NUMBER_PATTERN.matcher(value);
		return matcher.matches();
	}
	//=============dx
	/**
	 * 获取某个数值的百分比数值
	 * @param value
	 * @param percent
	 * @param base
	 * @return
	 */
	public static Number getNumberPercentValue(Number value, Number percent, Number base) {
		return new BigDecimal(value.doubleValue() * percent.doubleValue() / base.doubleValue()).setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	/**
	 * 获取保留几位小数点的数字
	 * @param number
	 * @param scale
	 * @return
	 */
	public static Number getScale(Number number, int scale) {
		return new BigDecimal(number.doubleValue()).setScale(scale, BigDecimal.ROUND_HALF_UP);
	}
	
	public static String format(int value, int width) {
		return String.format("%0" + width + "d", value);
	}
	
	public static String format(long value, int width) {
		return String.format("%0" + width + "d", value);
	}

	public static double retain(double fs) {
		return Math.floor(fs * 100) / 100;
	}
	
	/**
     * 当浮点型数据位数超过10位之后，数据变成科学计数法显示。用此方法可以使其正常显示。
     * @param value
     * @return Sting
     */
    public static String formatFloatNumber(double value) {
        if(value != 0.00){
            java.text.DecimalFormat df = new java.text.DecimalFormat("########.00");
            return df.format(value);
        }else{
            return "0.00";
        }
    }
}