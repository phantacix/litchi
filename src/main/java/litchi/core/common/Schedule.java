//-------------------------------------------------
// Litchi Game Server Framework
// Copyright(c) 2019 phantaci <phantacix@qq.com>
// MIT Licensed
//-------------------------------------------------
package litchi.core.common;

import litchi.core.common.thread.NamedThreadFactory;
import litchi.core.common.utils.DateUtils;
import litchi.core.common.thread.NamedThreadFactory;
import litchi.core.common.utils.DateUtils;
import litchi.core.common.utils.ServerTime;

import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * spring调度封装
 *
 * @author 0x737263
 */
public class Schedule {

    public static void main(String[] args) {
        Schedule schedule = new Schedule(5, "test");
        schedule.addEverySecond(() -> {
            System.out.println(DateUtils.formatTime(ServerTime.timeMillis(), DateUtils.PATTERN_NORMAL + ",SSS"));
        }, 1);
    }

    private ScheduledExecutorService executorService;

    public Schedule(int threadSize, String name) {
        executorService = Executors.newScheduledThreadPool(threadSize, new NamedThreadFactory(name));
    }

    /**
     * 每x毫秒钟执行（如果时间已过立即执行一次）
     *
     * @param task            任务
     * @param rateMillisecond 执行周期（毫秒）
     */
    public void addEveryMillisecond(Runnable task, long rateMillisecond) {
        executorService.scheduleAtFixedRate(task, 0, rateMillisecond, TimeUnit.MILLISECONDS);
    }

    public void addEveryMillisecond(Runnable task, long rateMillisecond, long delay) {
        executorService.scheduleAtFixedRate(task, delay, rateMillisecond, TimeUnit.MILLISECONDS);
    }

    /**
     * 每x秒钟执行（如果时间已过立即执行一次）
     *
     * @param task       任务
     * @param rateSecond 执行周期（秒）
     */
    public void addEverySecond(Runnable task, int rateSecond) {
        executorService.scheduleAtFixedRate(task, 0, rateSecond, TimeUnit.SECONDS);
    }

    public void addEverySecond(Runnable task, int rateSecond, long delay) {
        executorService.scheduleAtFixedRate(task, delay, rateSecond, TimeUnit.SECONDS);
    }

    /**
     * 每x分钟执行 （如果时间已过立即执行一次）
     *
     * @param task       runnable对象
     * @param rateMinute 执行周期时间(分钟)
     */
    public void addEveryMinute(Runnable task, int rateMinute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.MINUTE, 1);
        long initialDelay = calendar.getTimeInMillis() - DateUtils.getNowMillis();
        executorService.scheduleAtFixedRate(task, initialDelay, rateMinute, TimeUnit.MINUTES);
    }

    /**
     * 每小时整点触发(每天24次） 重复执行
     *
     * @param task 任务
     */
    public void addEveryHour(Runnable task) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.HOUR, 1);
        long initialDelay = calendar.getTimeInMillis() - DateUtils.getNowMillis();
        executorService.scheduleAtFixedRate(task, initialDelay, 1, TimeUnit.HOURS);
    }

    /**
     * 每天x点执行.(每天一次) （如果时间已过立即执行一次），然后延迟一天， 重复执行
     *
     * @param task
     * @param hour 1-24 小时定时执行
     */
    public void addFixedTime(Runnable task, int hour) {
        if (hour == 0) {
            hour = 24;
        }
        addFixedTime(task, hour, 0, 0);
    }

    /**
     * 每天x点执行.(每天一次) （如果时间已过立即执行一次），然后延迟一天， 重复执行
     *
     * @param task
     * @param hour
     * @param minutes
     * @param seconds
     */
    public void addFixedTime(Runnable task, int hour, int minutes, int seconds) {
        if (hour == 0) {
            hour = 24;
        }
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minutes);
        c.set(Calendar.SECOND, seconds);
        c.set(Calendar.MILLISECOND, 0);

        long delay = c.getTimeInMillis() - DateUtils.getNowMillis();
        delay = delay > 0 ? delay : DateUtils.DAY_MILLISECOND + delay;

        executorService.scheduleAtFixedRate(task, delay, DateUtils.DAY_MILLISECOND, TimeUnit.MILLISECONDS);
    }

    /**
     * 延迟执行
     *
     * @param task    任务
     * @param seconds 延迟时间(秒)
     */
    public void addDelaySeconds(Runnable task, int seconds) {
        executorService.schedule(task, seconds, TimeUnit.SECONDS);
    }

    public void shutdown() {
        try {
            if (this.executorService != null) {
                this.executorService.shutdown();
            }
        } catch (Exception ex) {
        }
    }

}
