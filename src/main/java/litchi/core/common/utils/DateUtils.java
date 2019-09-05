//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
//import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日期时间工具类
 * 
 * @author 0x737263
 * 
 */
public class DateUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(DateUtils.class);
	/** 1秒钟的毫秒数*/
	public static final int SECOND_MILLISECOND = 1000;
	/** 1分钟的毫秒数*/
	public static final int MINUTE_MILLISECOND = 60 * 1000;
	/** 1小时的毫秒数*/
	public static final long HOUR_MILLISECOND = 60 * MINUTE_MILLISECOND;
	/** 1天的毫秒数*/
	public static final long DAY_MILLISECOND = 24 * HOUR_MILLISECOND;
	/** 1分钟的秒数*/
	public static final long MINUTE_SECOND = 60;
	/** 1小时的秒数*/
	public static final long HOUR_SECOND = 60 * MINUTE_SECOND;
	/** 1天的秒数*/
	public static final long DAY_SECOND = 24 * HOUR_SECOND;
	/** HH:mm*/
	public static final String PATTERN_HH_MM = "HH:mm";
	/** HH:mm::ss*/
	public static final String PATTERN_HH_MM_SS = "HH:mm:ss";
	/** yyyyMMdd*/
	public static final String PATTERN_YYYYMMDD = "yyyyMMdd";
	/** yyyy-MM-dd*/
	public static final String PATTERN_YYYY_MM_DD = "yyyy-MM-dd";
	/** yyyyMMddHH*/
	public static final String PATTERN_YYYYMMDDHH = "yyyyMMddHH";
	/** yyyyMMddHHmm*/
	public static final String PATTERN_YYYYMMDDHHMM = "yyyyMMddHHmm";
	/** yyyy-MM-dd HH:mm:ss*/
	public static final String PATTERN_NORMAL = "yyyy-MM-dd HH:mm:ss";
	/** yyyyMMddHHmmss*/
	public static final String PATTERN_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
	/** 1周的总天数 */
	public static final int ONE_WEEK_DAY = 7;
	/** 1天的小时数 */
	public static final int ONE_DAY_HOUR = 24;

	public static Integer getYear() {
		return getCalendar().get(Calendar.YEAR);
	}

	/**
	 * 是不否为今天
	 * @param second    utc转换忧的秒
	 * @return
	 */
	public static boolean isToday(int second) {
		return isToday(second * 1000L);
	}
	
	public static Calendar getCalendar() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(ServerTime.timeMillis());
		return calendar;
	}

	/**
	 * 是不否为今天
	 * @param millis    毫秒
	 * @return
	 */
	public static boolean isToday(long millis) {
		Calendar today = getCalendar();
		Calendar compareday = getCalendar();
		compareday.setTimeInMillis(millis);
		if (today.get(Calendar.YEAR) == compareday.get(Calendar.YEAR) && today.get(Calendar.MONTH) == compareday.get(Calendar.MONTH)
				&& today.get(Calendar.DAY_OF_MONTH) == compareday.get(Calendar.DAY_OF_MONTH)) {
			return true;
		}
		return false;
	}

	/**
	 * 是否为昨天
	 * @param second
	 * @return
	 */
	public static boolean isYesterday(int second) {
		return isYesterday(second * 1000L);
	}

	/**
	 * 是否为昨天
	 * @param time
	 * @return
	 */
	public static boolean isYesterday(long time) {
		Calendar today = getCalendar();

		Calendar yestCalendar = getCalendar();
		long nowMills = today.getTimeInMillis();
		long yesterdayTime = nowMills - 86400000L;
		// 1天前的日期
		yestCalendar.setTimeInMillis(yesterdayTime);

		Calendar compareCalendar = getCalendar();
		compareCalendar.setTimeInMillis(time);

		if (yestCalendar.get(Calendar.YEAR) == compareCalendar.get(Calendar.YEAR)
				&& yestCalendar.get(Calendar.MONTH) == compareCalendar.get(Calendar.MONTH)
				&& yestCalendar.get(Calendar.DAY_OF_MONTH) == compareCalendar.get(Calendar.DAY_OF_MONTH)) {
			return true;
		}
		return false;
	}

	/**
	 * 获取今天指定时间的Date
	 * 
	 * @param hour
	 * @param minute
	 * @param seconds
	 * @param millSenconds
	 * @return
	 */
	public static Calendar getSpecialTimeOfToday(int hour, int minute, int seconds, int millSenconds) {
		Calendar cal = getCalendar();
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, seconds);
		cal.set(Calendar.MILLISECOND, millSenconds);
		return cal;
	}

	/**
	 * 日期转换为字符串
	 * 
	 * @param theDate       日期
	 * @param datePattern   {@code DatePattern}
	 * @return
	 */
	public static String date2String(Date theDate, String datePattern) {
		if (theDate == null) {
			return "";
		}

		DateFormat format = new SimpleDateFormat(datePattern);
		try {
			return format.format(theDate);
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		return "";
	}

	/**
	 * 字符串转换为Date对象
	 * @param dateString    日期字符串
	 * @param datePattern   {@code DatePattern}
	 * @return
	 */
	public static Date string2Date(String dateString, String datePattern) {
		if ((dateString == null) || (dateString.trim().isEmpty())) {
			return null;
		}

		DateFormat format = new SimpleDateFormat(datePattern);
		try {
			return format.parse(dateString);
		} catch (ParseException e) {
			LOGGER.error("ParseException in converting string to date: " + e.getMessage());
		}

		return null;
	}

	/**
	 * 毫秒转换为秒
	 * @param millis    毫秒
	 * @return
	 */
	public static long toSecond(long... millis) {
		long second = 0L;
		if ((millis != null) && (millis.length > 0)) {
			long[] arrayOfLong = millis;
			int j = millis.length;
			for (int i = 0; i < j; ++i) {
				long time = arrayOfLong[i];
				second += time / 1000L;
			}

		}
		return second;
	}

	/**
	 * 日期修改
	 * 
	 * @param theDate       日期
	 * @param addHours      增加天
	 * @param addMinutes    增加分
	 * @param addSecond     增加秒
	 * @return
	 */
	public static Date add(Date theDate, int addHours, int addMinutes, int addSecond) {
		if (theDate == null) {
			return null;
		}

		Calendar cal = getCalendar();
		cal.setTime(theDate);

		cal.add(11, addHours);
		cal.add(12, addMinutes);
		cal.add(13, addSecond);

		return cal.getTime();
	}

	/**
	 * 获取下一天的整点时间
	 * @param hour
	 * @return
	 */
	public static Date getNextDayHour(int hour) {
		Calendar cal = getCalendar();
		cal.setTimeInMillis(new Date().getTime() + DAY_MILLISECOND);
		return new GregorianCalendar(cal.get(1), cal.get(2), cal.get(5), hour, 0).getTime();
	}

	/**
	 * 获取今天的整点时间
	 * @return
	 */
	public static Date getTodayHour(int hour) {
		Calendar cal = getCalendar();
		cal.setTimeInMillis(new Date().getTime());
		return new GregorianCalendar(cal.get(1), cal.get(2), cal.get(5), hour, 0).getTime();
	}

	/**
	 * 当前时间转换为秒
	 * 
	 * @return
	 */
	public static long getCurrentSecond() {
		return System.currentTimeMillis(); // 转换为秒
	}

	/**
	 * 判断到目前为止是否超过了指定的时间区间
	 * 
	 * @param startTime
	 * @param timeInterval
	 * @return
	 */
	public static boolean beyondTheTime(long startTime, int timeInterval) {
		return getNow() - startTime >= timeInterval;
	}

	/**
	 * 判断到目前为止是否超过了指定的时间区间
	 * 
	 * @param startTime
	 * @param timeInterval
	 * @return
	 */
	public static boolean beyondTheTime(long startTime, long timeInterval) {
		return System.currentTimeMillis() - startTime >= timeInterval;
	}

	/**
	 * 判断是否在时间区间内
	 *
	 * @param startTime     开始时间
	 * @param endTime       结束时间
	 * @return
	 */
	public static boolean isActiveTime(int startTime, int endTime) {
		return startTime < getNow() && getNow() < endTime;
	}

	/**
	 * 判断是否在时间区间内
	 *
	 * @param startTime     开始时间(毫秒)
	 * @param endTime       结束时间(毫秒)
	 * @return
	 */
	public static boolean isActiveTime(long startTime, long endTime) {
		long now = System.currentTimeMillis();
		return startTime < now && now < endTime;
	}

	public static boolean isActiveTime(long startTime, long endTime, long now) {
		return startTime < now && now < endTime;
	}

	/**
	 * 判断是否应该触发每日事件(例如次数重置事件)
	 * 
	 * @param hour              事件应该被触发的时间,即配置时间(24小时制)
	 * @param lastOccurTime     最近一次实际触发的时间,单位：秒
	 * @return
	 */
	public static boolean isTime4DailyEvent(int hour, int lastOccurTime) {
		long secondes = lastOccurTime;
		// 取得今天的触发时间
		Calendar checkDate = DateUtils.getSpecialTimeOfToday(hour, 0, 0, 0);
		Calendar now = getCalendar();

		// 如果今天的触发时间未到,则取过去最近一次的可触发时间
		if (now.before(checkDate)) {
			checkDate.add(Calendar.DAY_OF_YEAR, -1);
		}

		// 如果过去最近一次触发时间事件未被触发,则判断为可触发
		if (new Date(secondes * 1000).before(checkDate.getTime())) {
			return true;
		}
		return false;
	}

	/**
	 * 获取当前时间的总秒数
	 * @return
	 */
	public static int getNowInSecondes() {
		return (int) (System.currentTimeMillis() / 1000);
	}

	/**
	 * @param delay
	 * @param timeUnit
	 * @return
	 */
	public static Date getDelayDate(int delay, TimeUnit timeUnit) {
		Calendar c = getCalendar();
		c.setTime(new Date());
		switch (timeUnit) {
		case DAYS:
			c.add(Calendar.DAY_OF_YEAR, delay);
			break;
		case HOURS:
			c.add(Calendar.HOUR_OF_DAY, delay);
			break;
		case MILLISECONDS:
			c.add(Calendar.MILLISECOND, delay);
			break;
		case MINUTES:
			c.add(Calendar.MINUTE, delay);
			break;
		case SECONDS:
			c.add(Calendar.SECOND, delay);
			break;
		default:
			LOGGER.warn(String.format("IllegalArgumentException:{%s}", timeUnit.toString()));
			break;
		}
		return c.getTime();
	}

	/**
	 * 延期执行(整数倍时间)
	 * <pre>例如：5分钟执行一次，当前时间为2015-8-24 15:48:22 开始时间为：2015-8-24 15:50:01</pre>
	 * <pre>    当前时间为2015-8-24 15:57:22 开始时间为：2015-8-24 16:00:01</pre>
	 * @param startMinute 分钟
	 * @return
	 */
	public static Date getDelayMinuteDate(int startMinute) {
		Calendar c = getCalendar();
		c.setTime(new Date());
		int m = c.get(Calendar.MINUTE);
		int minute = startMinute - (m % startMinute);
		c.add(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, 1);
		c.set(Calendar.MILLISECOND, 0);

		return c.getTime();
	}

	/**
	 * 到零晨还剩余多少秒
	 * 
	 * @return
	 */
	public static int residueSecond2NextDay0AM() {
		return (int) (getNextDayHour(0).getTime() - new Date().getTime()) / 1000;
	}

	/**
	 * 格式化date时间类型为yyyy-MM-dd HH:mm:ss格式
	 * 
	 * @param date
	 * @return
	 */
	public static String formatTime(Date date) {
		return formatTime(date, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 格式化时间
	 * @param date
	 * @param format
	 * @return
	 */
	public static String formatTime(Date date, String format) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		return simpleDateFormat.format(date);
	}

	/**
	 * 格式化时间
	 * @param time
	 * @param format
	 * @return
	 */
	public static String formatTime(long time, String format) {
		return formatTime(new Date(time), format);
	}

	/**
	 * 格式化date时间类型为yyyy-MM-dd HH:mm:ss格式
	 * 
	 * @param time
	 * @return
	 */
	public static String formatTime(long time) {
		return formatTime(new Date(time));
	}

	/**
	 * 当前时间是否超过了指定时间
	 * @param endTime
	 * @return
	 */
	public static boolean isExceedTime(Date endTime) {
		Date now = new Date();
		return endTime.getTime() < now.getTime();
	}

	/**
	 * 两个日期相差几天(按天数单位来比较)
	 * @param beginDate		开始日期
	 * @param endDate		结束日期
	 * @return
	 */
	public static int getRemainDays(Date beginDate, Date endDate) {
		Calendar beginCalendar = getCalendar();
		beginCalendar.setTime(beginDate);

		Calendar endCalendar = getCalendar();
		endCalendar.setTime(endDate);

		int days = endCalendar.get(Calendar.DAY_OF_YEAR) - beginCalendar.get(Calendar.DAY_OF_YEAR);

		int diffYears = endCalendar.get(Calendar.YEAR) - beginCalendar.get(Calendar.YEAR);
		if (diffYears > 0) {
			days += diffYears * 365;
		}
		return days;
	}

	/**
	 * 比较目标时间是否在指定的两个时间之间
	 * @param date1
	 * @param date2
	 * @param targetDate 目标时间
	 * @return
	 */
	public static boolean isValidTime(Date date1, Date date2, Date targetDate) {
		if (date1.before(date2)) {
			if (date1.before(targetDate) && date2.after(targetDate)) {
				return true;
			}
		} else {
			if (date2.before(targetDate) && date1.after(targetDate)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 比较目标时间是否在指定的两个时间之间
	 * @param dateStr1 日期字符串，格式：yyyy-MM-dd HH:mm:ss
	 * @param dateStr2 日期字符串，格式：yyyy-MM-dd HH:mm:ss
	 * @param targetDate 目标时间
	 * @return
	 */
	public static boolean isValidTime(String dateStr1, String dateStr2, Date targetDate) {
		Date date1 = string2Date(dateStr1, PATTERN_NORMAL);
		Date date2 = string2Date(dateStr2, PATTERN_NORMAL);
		return isValidTime(date1, date2, targetDate);
	}

	/**
	 * 根据当前时间获取的上一周
	 * @return
	 */
	public static Integer getPrevWeekTime() {
		int week = getWeekOfYear();
		if (week == 0) {
			return week;
		}
		return week - 1;
	}

	/**
	 * 获取当前时间为第几周
	 * @return
	 */
	public static Integer getWeekOfYear() {
		Calendar calendar = getCalendar();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);

		return calendar.get(Calendar.WEEK_OF_YEAR);
	}

	/**
	 * 获取当前时间为星期几(Calendar)
	 * @return
	 */
	public static Integer getDayOfWeek() {
		return getCalendar().get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 当前时间是否为指定的星期数
	 *
	 * @param week 请使用Calendar里的周一到周日的常量
	 * @return
	 */
	public static boolean isDayOfWeek(int week) {
		return getDayOfWeek() == week;
	}

	/**
	 * Sunday为星期天，数值为7 ,Monday为星期一,数值为 1
	 * @return
	 */
	public static int getChineseDayOfWeek() {
		Integer dayOfWeek = getDayOfWeek();
		if (dayOfWeek == 1) {
			dayOfWeek = 7;
		} else {
			dayOfWeek--;
		}
		return dayOfWeek;
	}

	/**
	 * Sunday为星期天，数值为7 ,Monday为星期一,数值为 1
	 * @param week     传输为Calendar.SUNDAY类似的
	 * @return
	 */
	public static Integer getChineseWeek(int week) {
		if (week == Calendar.SUNDAY) {
			return week + 6;
		} else {
			return week - 1;
		}
	}

	/**
	 * 当前时间为几点钟
	 * @return
	 */
	public static Integer getHourOfDay() {
		return getCalendar().get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * 当前小时内的的分钟数
	 * @return
	 */
	public static Integer getMinuteOfHour() {
		return getCalendar().get(Calendar.MINUTE);
	}

	/**
	 * 获取本周内第几天
	 * @param millis
	 * @param firstDayOfWeek
	 * @return
	 */
	public static int getDayOfWeek(long millis, int firstDayOfWeek) {
		Calendar calendar = getCalendar();
		calendar.setTimeInMillis(millis);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek - firstDayOfWeek >= 0) {
			dayOfWeek = dayOfWeek - firstDayOfWeek + 1;
		} else {
			dayOfWeek = ONE_WEEK_DAY + (dayOfWeek - firstDayOfWeek + 1);
		}
		return dayOfWeek;
	}

	/**
	 * 获取本周内剩余天数
	 * @param millis
	 * @return
	 */
	public static int getRemainDayOfWeek(long millis) {
		// 这里暂时用中国的算法.周一为每周的第一天
		int dayOfWeek = getDayOfWeek(millis, Calendar.MONDAY);
		return ONE_WEEK_DAY - dayOfWeek;
	}

	/**
	 * 获取月底的日历
	 * @return
	 */
	public static Calendar getEndOfMonth() {
		Calendar calendar = getCalendar();
		calendar.add(Calendar.MONTH, 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		return calendar;
	}

	/**
	 * 两个时间是否在同一天
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static boolean isSameDay(long time1, long time2) {
		Calendar c1 = getCalendar();
		c1.setTimeInMillis(time1);
		Calendar c2 = getCalendar();
		c2.setTimeInMillis(time2);
		if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)) {
			return true;
		}
		return false;
	}

	/**
	 * 两个时间点是否在同一个小时内
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static boolean isInAHour(long time1, long time2) {
		Calendar c1 = getCalendar();
		c1.setTimeInMillis(time1);
		Calendar c2 = getCalendar();
		c2.setTimeInMillis(time2);
		if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
				&& c1.get(Calendar.HOUR_OF_DAY) == c2.get(Calendar.HOUR_OF_DAY)) {
			return true;
		}
		return false;
	}

	/**
	 * 字符串转化为Calendar对象
	 * @param dateString
	 * @param datePattern
	 * @return
	 */
	public static Calendar string2Calendar(String dateString, String datePattern) {
		Date date = string2Date(dateString, datePattern);
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		return calendar;
	}

	/**
	 * 获取现在的Calendar对象(参数里面的只有hour,minute)
	 * @param calendar
	 * @return
	 */
	public static Calendar getNowCalendar(Calendar calendar) {
		Calendar timeCalendar = getCalendar();
		timeCalendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
		timeCalendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
		timeCalendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND));
		timeCalendar.set(Calendar.MILLISECOND, calendar.get(Calendar.MILLISECOND));
		return timeCalendar;
	}

	/**
	 * 获取下周一0时
	 * @param millis
	 * @return
	 */
	public static Date getNextWeekMonday0AM(long millis) {
		Calendar calendar = getCalendar();
		calendar.setTimeInMillis(millis);
		calendar.add(Calendar.WEEK_OF_YEAR, 1);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	/**
	 * 两个时间是否在同一周
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static boolean isSameWeek(long time1, long time2) {
		Calendar c1 = getCalendar();
		c1.setTimeInMillis(time1);
		Calendar c2 = getCalendar();
		c2.setTimeInMillis(time2);
		if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.WEEK_OF_YEAR) == c2.get(Calendar.WEEK_OF_YEAR)) {
			return true;
		}
		return false;
	}

	/**
	 * 获取次日凌晨时间(秒)
	 * 
	 * @return
	 */
	public static int getEarlyMorning() {
		return (int) (getEarlyMorningMillis() / 1000);
	}

	/**
	 * 获取次日凌晨时间(毫秒)
	 * 
	 * @return
	 */
	public static Long getEarlyMorningMillis() {
		Calendar now = getCalendar();
		now.add(Calendar.DAY_OF_MONTH, 1);
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		return now.getTimeInMillis();
	}

	/**
	 * 是否是今日零点以前
	 * 
	 * @param time
	 * @return
	 */
	public static boolean beforeTodayZero(int time) {
		Calendar specifyTime = getCalendar();
		specifyTime.set(Calendar.HOUR_OF_DAY, 0);
		specifyTime.set(Calendar.MINUTE, 0);
		specifyTime.set(Calendar.SECOND, 0);
		specifyTime.set(Calendar.MILLISECOND, 0);// 今日零点时间，昨日的终点时间
		if (specifyTime.getTimeInMillis() / 1000 < time) {
			return false;
		}
		return true;
	}

	/**
	 * 获取当前时间( UTC 1970 秒)
	 * 
	 * @return
	 */
	public static int getNow() {
		return (int) (System.currentTimeMillis() / 1000);
	}

	public static long getNowMillis() {
		return System.currentTimeMillis();
	}

	/**
	 * 获取当前时间(2013-04-11 00:00:09)
	 * 
	 * @return
	 */
	public static String getTextNow() {
		return DateUtils.formatTime(getCalendar().getTime());
	}

	/**
	 * 毫秒换算成秒
	 * 
	 * @param timeMillis
	 * @return
	 */
	public static int getSecond(long timeMillis) {
		return (int) (timeMillis / 1000);
	}

	/**
	 * 获取当前时间( UTC 1970 秒)数组
	 * 
	 * @return
	 * @throws IOException
	 */
	public static byte[] getNowBytes() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		dos.writeLong(System.currentTimeMillis());
		return baos.toByteArray();
	}

	/**
	 * 是否在当月时间范围内 这个月第一天的0:00:00至下个月第一天的0:00:00
	 * 
	 * @param time
	 *            utc时间转换为秒
	 * @return
	 */
	public static boolean inMonth(int time) {
		Calendar zeroTime = getCalendar();// 本月初零点
		zeroTime.set(Calendar.DAY_OF_MONTH, 1);
		zeroTime.set(Calendar.HOUR_OF_DAY, 0);
		zeroTime.set(Calendar.MINUTE, 0);
		zeroTime.set(Calendar.SECOND, 0);
		zeroTime.set(Calendar.MILLISECOND, 0);
		int monthStartTime = (int) (zeroTime.getTimeInMillis() / 1000);
		zeroTime.add(Calendar.MONTH, 1);// 本月末零点
		int monthEndTime = (int) (zeroTime.getTimeInMillis() / 1000);
		if (monthStartTime < time && time < monthEndTime) {
			return true;
		}
		return false;
	}

	/**
	 * 是否在昨日时间范围内
	 * 
	 * @param time
	 *            utc时间转换为秒
	 * @return
	 */
	public static boolean inYesterday(int time) {
		return inYesterday(time * 1000L);

	}

	/**
	 * 是否在昨日时间范围内
	 * 
	 * @param time
	 *            utc时间转换为毫秒
	 * @return
	 */
	public static boolean inYesterday(long time) {
		Calendar yesterday = getCalendar();
		yesterday.set(Calendar.DATE, yesterday.get(Calendar.DATE) - 1);
		Calendar compareday = getCalendar();
		compareday.setTimeInMillis(time);

		if (yesterday.get(Calendar.YEAR) == compareday.get(Calendar.YEAR)
				&& yesterday.get(Calendar.DAY_OF_MONTH) == compareday.get(Calendar.DAY_OF_MONTH)) {
			return true;
		}

		return false;

	}

	/**
	 * 根据当前时间获取一个整点时间
	 * 
	 * @return 返回utc时间毫秒
	 */
	public static long getNextHourTime() {
		Calendar nowDay = getCalendar();
		int minute = nowDay.get(Calendar.MINUTE);
		int second = nowDay.get(Calendar.SECOND);
		int millSecond = nowDay.get(Calendar.MILLISECOND);
		if (minute == 0 && second == 0 && millSecond == 0) {
			return nowDay.getTimeInMillis();
		}

		nowDay.add(Calendar.HOUR_OF_DAY, 1);
		nowDay.set(Calendar.MINUTE, 0);
		nowDay.set(Calendar.SECOND, 0);
		nowDay.set(Calendar.MILLISECOND, 0);

		return nowDay.getTimeInMillis();
	}

	/**
	 * 当前几点(24小时制)
	 * 
	 * @return
	 */
	public static int getHour() {
		Calendar nowDay = getCalendar();
		return nowDay.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * 获取整点时间
	 * 
	 * @param hour
	 *            设置整点(0-23)
	 * @return 返回utc时间毫秒
	 */
	public static long setFixTime(int hour) {
		Calendar currentTime = getCalendar();
		int curentHour = currentTime.get(Calendar.HOUR_OF_DAY);
		if (curentHour >= hour) {
			hour += 24;
		}
		Calendar nowDay = getCalendar();
		nowDay.set(Calendar.HOUR_OF_DAY, hour);
		nowDay.set(Calendar.MINUTE, 0);
		nowDay.set(Calendar.SECOND, 0);
		nowDay.set(Calendar.MILLISECOND, 0);

		return nowDay.getTimeInMillis();
	}

	/**
	 * 获取两个时间差值（小时）
	 * 
	 * @param startSeconds
	 *            开始时间
	 * @param endSeconds
	 *            结束时间
	 * @return 小时
	 */
	public static int getBetweenHour(int startSeconds, int endSeconds) {
		int result = (endSeconds - startSeconds) / 3600;
		return result;
	}

	/**
	 * 获取两个时间差值（小时）
	 * 
	 * @param startMS
	 *            开始时间毫秒
	 * @param endMS
	 *            结束时间毫秒
	 * @return 小时
	 */
	public static int getBetweenHour(long startMS, long endMS) {
		int result = (int) ((endMS - startMS) / 3600000);
		return result;
	}

	/**
	 * 获取两个时间差值(分钟)
	 * 
	 * @param starMillis
	 * @param endMillis
	 * @return
	 */
	public static int getBetweenMinute(long starMillis, long endMillis) {
		int result = (int) (endMillis - starMillis) / MINUTE_MILLISECOND;
		return result;
	}

	/**
	 * 获取两个时间差值(秒)
	 * 
	 * @param starMillis
	 * @param endMillis
	 * @return
	 */
	public static int getBetweenSecond(long starMillis, long endMillis) {
		int result = (int) ((endMillis - starMillis) / SECOND_MILLISECOND);                           
		return result;
	}

	/**
	 * 获取两个时间差值(毫秒)
	 * 
	 * @param starMillis
	 * @param endMillis
	 * @return
	 */
	public static int getBetweenMillis(long starMillis, long endMillis) {
		int result = (int) (endMillis - starMillis);
		return result;
	}

	/**
	 * 计算两个毫秒时间的天数差 2016-01-01 23:59:59 与 2016-01-02 00:00:00 差一天
	 * 
	 * @param startMS
	 * @param endMS
	 * @return
	 */
	public static int getBetweenDays(long startMS, long endMS) {
		Calendar cal = getCalendar();
		cal.setTimeInMillis(startMS);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		startMS = cal.getTimeInMillis();
		cal.setTimeInMillis(endMS);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		endMS = cal.getTimeInMillis();
		return (int) ((endMS - startMS) / (1000 * 3600 * 24));
	}

	/**
	 * 获取今天的零点
	 */
	public static int getTodayZero() {
		Calendar zeroTime = getCalendar();
		zeroTime.set(Calendar.HOUR_OF_DAY, 0);
		zeroTime.set(Calendar.MINUTE, 0);
		zeroTime.set(Calendar.SECOND, 0);
		zeroTime.set(Calendar.MILLISECOND, 0);
		return (int) (zeroTime.getTimeInMillis() / 1000);
	}

	/**
	 * 获取今天的零点
	 */
	public static long getTodayZeroMillis() {
		Calendar zeroTime = getCalendar();
		zeroTime.set(Calendar.HOUR_OF_DAY, 0);
		zeroTime.set(Calendar.MINUTE, 0);
		zeroTime.set(Calendar.SECOND, 0);
		zeroTime.set(Calendar.MILLISECOND, 0);
		return zeroTime.getTimeInMillis();
	}

	/**
	 * 获取昨天的零点
	 */
	public static long getYesterDayZero() {
		Calendar zeroTime = getCalendar();
		zeroTime.add(Calendar.DAY_OF_MONTH, -1);
		zeroTime.set(Calendar.MINUTE, 0);
		zeroTime.set(Calendar.SECOND, 0);
		zeroTime.set(Calendar.MILLISECOND, 0);
		return zeroTime.getTimeInMillis();
	}

	/**
	 * 获取昨天的23点59分59秒
	 */
	public static long getYesterDayFinal() {
		Calendar zeroTime = getCalendar();
		zeroTime.add(Calendar.DAY_OF_MONTH, -1);
		zeroTime.set(Calendar.HOUR_OF_DAY, 23);
		zeroTime.set(Calendar.MINUTE, 59);
		zeroTime.set(Calendar.SECOND, 59);
		zeroTime.set(Calendar.MILLISECOND, 0);
		return zeroTime.getTimeInMillis();
	}

	public static long getMillisecondOfDay(long ms) {
		Calendar calendar = getCalendar();
		calendar.setTimeInMillis(ms);
		calendar.set(11, 0);
		calendar.set(12, 0);
		calendar.set(13, 0);
		calendar.set(14, 0);
		return ms - calendar.getTimeInMillis();
	}
	//=====================dx
	public static final SimpleDateFormat SDF_SHORT_DATE = new SimpleDateFormat(PATTERN_YYYY_MM_DD);
	public static final SimpleDateFormat SDF_LONG_DATE = new SimpleDateFormat(PATTERN_NORMAL);
	public static final SimpleDateFormat SDF_TIME = new SimpleDateFormat(PATTERN_HH_MM);
	
	public static Date getDateWithDifferDay(int differDay) {
		Calendar calendar = getCalendar();
		calendar.add(Calendar.DAY_OF_YEAR, differDay);
		return new Date(calendar.getTimeInMillis());
	}

	public static Date getDateWithDifferDay(Date current, int differDay) {
		if (current != null) {
			return getDateWithDifferDay(current.getTime(), differDay);
		} else {
			return getDateWithDifferDay(differDay);
		}
	}

	public static Date getDateWithDifferDay(long current, int differDay) {
		Calendar calendar = getCalendar();
		calendar.setTimeInMillis(current);
		calendar.add(Calendar.DAY_OF_YEAR, differDay);
		return new Date(calendar.getTimeInMillis());
	}

	public static Date getDayEnd(long current) {
		Calendar calendar = getCalendar();
		calendar.setTimeInMillis(current);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 995);
		return new Date(calendar.getTimeInMillis());
	}
	
	public static Calendar getTodayEnd() {
		return getSpecialTimeOfToday(23, 59, 59, 999);
	}
	
	private static void setDateToZeroHMS(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
	}
	
	public static Date getDayStart(long current) {
		Calendar calendar = getCalendar();
		calendar.setTimeInMillis(current);
		setDateToZeroHMS(calendar);
		return new Date(calendar.getTimeInMillis());
	}
	
	public static Date getDayStart() {
		return getDayStart(System.currentTimeMillis());
	}
	
	public static Calendar getTodayStart() {
		return getSpecialTimeOfToday(0, 0, 0, 0);
	}
	
	/**
	 * 根据小时分钟获取时间
	 * @param timeStr
	 * @return
	 */
	public static long getDateWithHHmm(String timeStr) {
		return getDateWithHHmm(timeStr, 0);
	}
	/**
	 * 根据小时分钟获取时间
	 * @param timeStr
	 * @param delayDay
	 * @return
	 */
	public static long getDateWithHHmm(String timeStr, int delayDay) {
		String[] strArray = timeStr.split(":");
		int hour = Integer.parseInt(strArray[0]);
		int minute = Integer.parseInt(strArray[1]);
		Calendar calendar = getCalendar();
		calendar.add(Calendar.DAY_OF_YEAR, delayDay);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}
	
	public static Date getDayStart(Date current) {
		if (current != null) {
			return getDayStart(current.getTime());
		} else {
			return getDayStart();
		}
	}

	public static long getWeekMonday0AM(long time) {
		Calendar calendar = getCalendar();
		calendar.setTimeInMillis(time);
		if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			calendar.add(Calendar.DAY_OF_WEEK, -1);
		}
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTimeInMillis();
	}

	public static long parse(String string, String pattern) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		return dateFormat.parse(string).getTime();
	}
	
	public static boolean isNeedReset(int resetHour, long now, long lastTime) {
		Calendar calendar = getCalendar();
		calendar.set(Calendar.HOUR_OF_DAY, resetHour);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		long todayResetTime = calendar.getTimeInMillis();
		if (now < todayResetTime) {
			calendar.add(Calendar.DAY_OF_YEAR, -1);
			long yesterdayResetTime = calendar.getTimeInMillis();
			if (lastTime < yesterdayResetTime) {
				return true;
			} else {
				return false;
			}
		} else {
			if (lastTime < todayResetTime) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	/**
	 * 将一个累积时长转换为 *天*时*分*秒
	 * @param time	累积毫秒数
	 * @return
	 */
	public static String timeFormat(long time) {
		long day = time / DAY_MILLISECOND;
		long remainH = time % DAY_MILLISECOND;
		long hour = remainH / HOUR_MILLISECOND;
		long remainM = remainH % HOUR_MILLISECOND;
		long minute = remainM / MINUTE_MILLISECOND;
		long remainS = remainM % MINUTE_MILLISECOND;
		long second = remainS / SECOND_MILLISECOND;
		if (day > 0) {
			return day + "天" + hour + "时" + minute + "分" + second + "秒"; 
		}
		if (hour > 0) {
			return hour +"时" + minute + "分" + second + "秒"; 
		}
		if (minute > 0) {
			return  minute + "分" + second + "秒"; 
		}
		return second + "秒"; 
	}
}