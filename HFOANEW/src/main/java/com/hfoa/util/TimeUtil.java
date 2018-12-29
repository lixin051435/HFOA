package com.hfoa.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * @author wzx
 * 时间工具类
 */
public class TimeUtil {
	/**
	 * 获取当前日期是星期几
	 * @param date
	 * @return 当前日期是星期几
	 */
	public static String getWeekOfDate(Date date) {
		String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
			w = 0;
		return weekDays[w];
	}
	/**
	 * String类型时间相减，获取小时数
	 * @param String
	 * @return 小时数
	 */
	public static int fromDateStringToHour(String beginTime,String endTime) { // 此方法计算时间毫秒
		Date beginDate = null; // 定义开始时间类型
		Date endDate = null; // 定义结束时间类型
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			beginDate = inputFormat.parse(beginTime); // 将字符型转换成日期型
			endDate=inputFormat.parse(endTime);
		} catch (Exception e) {
		e.printStackTrace();
		}
		
		long ss = (endDate.getTime() - beginDate.getTime()) / 1000; // 共计秒数
		int MM = (int) ss / 60; // 共计分钟数
		int hh = (int) ss / 3600; // 共计小时数
		int dd = (int) hh / 24; // 共计天数
		return hh; // 返回小时数
	}
	/**
	 * String类型时间相减，获取小时数
	 * @param String
	 * @return 精确到小数位
	 */
	public static double getAccurateHour (String beginTime,String endTime) { // 此方法计算时间毫秒
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		double accurateHour = 0;
		try {
		 Date d1 = df.parse(endTime);
		 Date d2 = df.parse(beginTime);  
	     long diff1 = d1.getTime() - d2.getTime();//这样得到的差值是毫秒级别  
	     int diff=(int) diff1;
	     int days = diff / (1000 * 60 * 60 * 24);  
	     int hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);  
	     int minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);  
	     accurateHour=days*24+hours+minutes*1.0/60;
//	     System.out.println(""+days+"天"+hours+"小时"+minutes+"分");
		} catch (ParseException e) {
			e.printStackTrace();
		}  
	      
	    return accurateHour;
	}
	/**
	 * 时间类型转换为String
	 * @param date
	 * @return String
	 */
	public static String fromDateDateToString(Date date) { // 此方法计算时间毫秒
		String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
		return dateStr; // 字符串形式时间
	}
	 /**
     * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
     * 
     * @param nowTime 当前时间
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     * @author wzx
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * 判断时间段是否有交集
     */
	public static boolean isOverlap(String startdate1, String enddate1,String startdate2, String enddate2) { 
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date leftStartDate = null;   
    	Date leftEndDate = null;   
    	Date rightStartDate = null;   
    	Date rightEndDate = null;  
    	try {    
    		leftStartDate = format.parse(startdate1);  
    		leftEndDate = format.parse(enddate1);     
    		rightStartDate = format.parse(startdate2);     
    		rightEndDate = format.parse(enddate2);
    		} catch (ParseException e) {  
    			return false;   
    		}    
//    	return ((leftStartDate.getTime() < rightStartDate.getTime()) && leftStartDate.getTime() >  rightEndDate.getTime()) ||((leftEndDate.getTime() <rightStartDate.getTime())&& leftEndDate.getTime() >=rightEndDate.getTime())||(( leftStartDate.getTime() >  rightStartDate.getTime())&& leftEndDate.getTime() <rightEndDate.getTime())||((leftStartDate.getTime() == rightStartDate.getTime())&& leftStartDate.getTime() >=   rightEndDate.getTime());    
    	//或者
    	return 	((rightStartDate.getTime() < leftStartDate.getTime())&& rightEndDate.getTime() > leftStartDate.getTime())||((rightStartDate.getTime() >  leftStartDate.getTime())&& rightStartDate.getTime() <  leftEndDate.getTime())||((rightStartDate.getTime() ==  leftStartDate.getTime())&& rightEndDate.getTime() >=  leftStartDate.getTime());
    	}
//    }
//    }

}
