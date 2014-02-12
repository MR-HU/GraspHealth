package com.innouni.health.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;

/**
 * {@code CalendaUtil} 日历工具类
 * 
 * @author HuGuojun
 * @date 2014-1-8 下午12:03:20
 * @modify
 * @version 1.0.0
 */
@SuppressLint("SimpleDateFormat")
public class CalendaUtil {

	/**
	 * 获取当前日期 Date---->2014-08-23
	 * 
	 * @return String
	 * @exception
	 */
	public static String getCurrentDate() {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(date);
	}

	/**
	 * 格式化时间:2014-08-23---->Date
	 * 
	 * @description getDateFromString
	 * @param strDate
	 * @return Date
	 */
	public static Date getDateFromString(String strDate) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date date = format.parse(strDate);
			return date;
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 格式化时间
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @return Date
	 */
	public static Date getFormatDate(int year, int month, int day) {
		String strDate = getStringDateFromYmd(year, month, day);
		return getDateFromString(strDate);
	}

	/**
	 * 
	 * @description getStringDateFromYmd
	 * @param year
	 * @param month
	 * @param day
	 * @return 2014-08-30
	 * @exception
	 */
	public static String getStringDateFromYmd(int year, int month, int day) {
		String y = String.valueOf(year);
		String m = String.valueOf(month);
		if (month < 10) {
			m = "0" + m;
		}
		String d = String.valueOf(day);
		if (day < 10) {
			d = "0" + d;
		}
		return y + "-" + m + "-" + d;
	}

	/**
	 * 获取某一天的前一天
	 * 
	 * @param specifiedDay(yyyy-MM-dd)
	 * @return (yyyy-MM-dd)
	 * @throws Exception
	 */
	public static String getSpecifiedDayBefore(String specifiedDay) {
		Calendar c = Calendar.getInstance();
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(specifiedDay);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		c.setTime(date);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day - 1);
		String dayBefore = new SimpleDateFormat("yyyy-MM-dd").format(c
				.getTime());
		return dayBefore;
	}

	/**
	 * 获取某一天的后一天
	 * 
	 * @param specifiedDay(yyyy-MM-dd)
	 * @return (yyyy-MM-dd)
	 */
	public static String getSpecifiedDayAfter(String specifiedDay) {
		Calendar c = Calendar.getInstance();
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(specifiedDay);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		c.setTime(date);
		int day = c.get(Calendar.DATE);
		c.set(Calendar.DATE, day + 1);
		String dayAfter = new SimpleDateFormat("yyyy-MM-dd")
				.format(c.getTime());
		return dayAfter;
	}

	/**
	 * 根據年月获取当月天数
	 * 
	 * @param year
	 * @param month
	 * @return int
	 */
	public static int getDaysOfMonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		int days = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		return days;
	}

	/**
	 * 获取某一天是星期几
	 * 
	 * @param specifiedDay(yyyy-MM-dd)
	 * @return String
	 * @exception
	 */
	public static String getWeek(String specifiedDay) {
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(specifiedDay);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		SimpleDateFormat format = new SimpleDateFormat("EEEE");
		String week = format.format(date);
		return week;
	}

	/**
	 * 20140825----->2014年08月25日
	 * 
	 * @description getFormatCnTime
	 * @param date
	 * @return String
	 */
	public static String getFormatCnTime(String date) {
		String year = date.substring(0, 4);
		String month = date.substring(4, 6);
		String day = date.substring(6, 8);
		return year + "年" + month + "月" + day + "日";
	}

}
