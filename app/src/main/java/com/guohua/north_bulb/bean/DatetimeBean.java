package com.guohua.north_bulb.bean;

import java.util.Calendar;

/**
 * @author Leo
 *         #time 2016-09-02
 *         #detail 日期时间
 */
public class DatetimeBean{
    public int year;
    public int month;
    public int day;
    public int tmpDay;//调整时间时选择时间值小于当前时间时，需要跳至第二天的时间值
    public int hour;
    public int minute;

    public DatetimeBean() {
        Calendar calendar = Calendar.getInstance();
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH);
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
        this.tmpDay = calendar.get(Calendar.DAY_OF_MONTH);
        this.hour = calendar.get(Calendar.HOUR_OF_DAY);
        this.minute = calendar.get(Calendar.MINUTE);
    }


    @Override
    public String toString() {
//        return year + "年" + (month + 1) + "月" + day + "日" + " " + hour + "时" + minute + "分";
        return year + "-" + (month + 1) + "-" + tmpDay + " " + " " + hour + ":" + minute + "";
    }

    /**
     * 获取设定日期距离现在的秒数
     *
     * @return
     */
    public long getTimeInMillis() {
        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();
        calendar.set(this.year, this.month, this.day, this.hour, this.minute);
        long future = calendar.getTimeInMillis();
        return future - now;
    }

    /**
     * 获取次日设定日期距离现在的秒数
     *
     * @return
     */
    public long getNextDayTimeInMillis() {
        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();
        calendar.set(this.year, this.month, this.day+1, this.hour, this.minute);
        long future = calendar.getTimeInMillis();
        return future - now;
    }
}