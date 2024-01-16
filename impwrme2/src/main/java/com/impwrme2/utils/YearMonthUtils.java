package com.impwrme2.utils;

import java.time.YearMonth;

public final class YearMonthUtils {

    /**
     * Format a YearMonth object into 'MM YYYY'
     * @param yearMonth Object to be converter.
     * @return a formatted String.
     */
    public static String getStringInFormatMM_YYYYFromYearMonth(YearMonth yearMonth) {
    	if (null == yearMonth) return "";
    	String year = String.valueOf(yearMonth.getYear());
    	String month = String.valueOf(yearMonth.getMonth().getValue());
    	return ("00" + month).substring(month.length()) + " " + year;
    }
    
    /**
     * Create a YearMonth object based on the String format 'MM YYYY'.
     * @param yearMonthStr String in format 'MM YYYY'.
     * @return a new instance of YearMonth.
     */
    public static YearMonth getYearMonthFromStringInFormatMM_YYYY(String yearMonthStr) {
    	if ((null == yearMonthStr) || yearMonthStr.length() == 0) return null;
    	String yearStr = yearMonthStr.substring(yearMonthStr.length()-4);
    	int year = Integer.valueOf(yearStr).intValue();
    	String monthStr = yearMonthStr.substring(0, 2).stripTrailing();
    	int month = Integer.valueOf(monthStr).intValue();
    	return YearMonth.of(year, month);
    }

	public static Integer getIntegerInFormatYYYYMMFromYearMonth(YearMonth yearMonth) {
		return (yearMonth.getYear() * 100) + yearMonth.getMonth().getValue();
	}

	public static YearMonth getYearMonthFromIntegerInFormatYYYYMM(Integer yearMonthInt) {
		int year = yearMonthInt / 100;
		int month = yearMonthInt % 100;
		return YearMonth.of(year, month);
	}

    public static String getStringInFormatMM_YYYYForEndOfYear(YearMonth yearMonth) {
    	String year = String.valueOf(yearMonth.getYear());
    	String month = String.valueOf(12);
    	return ("00" + month).substring(month.length()) + " " + year;
    }    
}