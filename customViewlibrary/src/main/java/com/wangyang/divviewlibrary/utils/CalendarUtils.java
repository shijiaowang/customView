package com.wangyang.divviewlibrary.utils;

/**
 * Created by wangyang on 2017/5/10.
 * email:1440214507@qq.com
 * 日历工具类
 */

import java.util.Calendar;
import java.util.Date;

public class CalendarUtils {
    /**
     * 几天几夜
     * @param start 开始
     * @param end 结束
     * @return 几天几夜
     */
    public static String getHowDayHowNight(String start,String end){
        String day="";
        String night="";
        try {
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(new Date(Long.parseLong(start)));
            int startDay = calendar.get(Calendar.DAY_OF_YEAR);
            int startYear = calendar.get(Calendar.YEAR);
            calendar.setTime(new Date(Long.parseLong(end)));
            int endDay = calendar.get(Calendar.DAY_OF_YEAR);
            int endYear = calendar.get(Calendar.YEAR);
            if (startYear==endYear){
                day=(endDay-startDay+1)+"";
                night=(endDay-startDay)+"";
            }else if (endYear>startYear){
                int sum=0;
                for (int i=startYear;i<endYear;i++){//如果是闰年
                    if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {
                        sum+=(366-startDay+1);
                    }else {
                        sum+=(365-startDay+1);
                    }
                    startDay=0;
                }
                sum+=endDay;
                day=sum+"";
                night=(sum-1)+"";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return day+"天"+night+"夜";
    }

    /**
     * 还有多少天
     * @param time 开始天数
     * @param time2 结束天数
     * @return 多少天
     */
    public static int getHowDay(String time,String time2){
        int day=0;
        try {
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(new Date(Long.parseLong(time)));
            int startDay = calendar.get(Calendar.DAY_OF_YEAR);
            int startYear = calendar.get(Calendar.YEAR);
            calendar.setTime(new Date(Long.parseLong(time2)));
            int endDay = calendar.get(Calendar.DAY_OF_YEAR);
            int endYear = calendar.get(Calendar.YEAR);
            if (startYear==endYear){
                day=(endDay-startDay+1);
            }else if (endYear>startYear){
                int sum=0;
                for (int i=startYear;i<endYear;i++){//如果是闰年
                    if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {
                        sum+=(366-startDay+1);
                    }else {
                        sum+=(365-startDay+1);
                    }
                    startDay=0;
                }
                sum+=endDay;
                day=sum;
            }
        }catch (Exception e){
            e.printStackTrace();
            day=0;
        }
        if(day<0)day=0;
        return day;
    }

    /**
     *  获取该用户多少岁
     * @param date 生日
     * @return 返回岁数
     */
    public  static int getAge(Date date) {
        Date nowData = new Date();
        Calendar start = Calendar.getInstance();
        start.setTime(date);
        Calendar now = Calendar.getInstance();
        now.setTime(nowData);
        int startYear = start.get(Calendar.YEAR);
        int startMonth = start.get(Calendar.MONTH);
        int startDay = start.get(Calendar.DAY_OF_MONTH);
        int nowYear = now.get(Calendar.YEAR);
        int nowMonth = now.get(Calendar.MONTH);
        int nowDay = now.get(Calendar.DAY_OF_MONTH);
        if (nowYear < startYear) return 0;
        int age = nowYear - startYear;
        if (nowMonth < startMonth) {
            age--;
        } else if (nowMonth == startMonth) {
            if (nowDay < startDay) {
                age--;
            }
        }
        return age;
    }
}