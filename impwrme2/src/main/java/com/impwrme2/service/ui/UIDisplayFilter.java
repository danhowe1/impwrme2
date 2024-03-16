package com.impwrme2.service.ui;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class UIDisplayFilter {

	public static final String DISPLAY_STYLE_CHART="DISPLAY_STYLE_CHART";
	public static final String DISPLAY_STYLE_BALANCES_TABLE="DISPLAY_STYLE_BALANCES_TABLE";
	public static final String ASSET_TYPE_ALL="ASSET_TYPE_ALL";
	public static final String ASSET_TYPE_LIQUID="ASSET_TYPE_LIQUID";
	public static final String BREAKDOWN_AGGREGATE="BREAKDOWN_AGGREGATE";
	public static final String BREAKDOWN_BY_RESOURCE="BREAKDOWN_BY_RESOURCE";
	public static final String YEAR_START="YEAR_START";
	public static final String YEAR_END="YEAR_END";
	public static final String TIME_PERIOD_MONTHLY="TIME_PERIOD_MONTHLY";
	public static final String TIME_PERIOD_ANNUALLY="TIME_PERIOD_ANNUALLY";
	public static final String DISPLAY_LEVEL_ALL="DISPLAY_LEVEL_ALL";
	public static final String DISPLAY_LEVEL_BY_NAME="DISPLAY_LEVEL_BY_NAME";
	public static final String DISPLAY_LEVEL_BY_TYPE="DISPLAY_LEVEL_BY_TYPE";
	public static final String DISPLAY_LEVEL_BALANCES_ONLY="DISPLAY_LEVEL_BALANCES_ONLY";
	
	private String displayStyle = DISPLAY_STYLE_CHART;
	private boolean assetTypeLiquid = true;
	private boolean breakdownAggregate = true;
	private boolean timePeriodAnnually = true;
	
	private YearMonth yearStart;
	private YearMonth yearEnd;
	private List<String> yearList = new ArrayList<String>();

	private String displayLevel = DISPLAY_LEVEL_BALANCES_ONLY;
	
	public String getDisplayStyle() {
		return displayStyle;
	}

	public void setDisplayStyle(String displayStyle) {
		this.displayStyle = displayStyle;
	}

	public boolean isAssetTypeLiquid() {
		return assetTypeLiquid;
	}

	public void setAssetTypeLiquid(boolean assetTypeLiquid) {
		this.assetTypeLiquid = assetTypeLiquid;
	}

	public boolean isBreakdownAggregate() {
		return breakdownAggregate;
	}

	public void setBreakdownAggregate(boolean breakdownAggregate) {
		this.breakdownAggregate = breakdownAggregate;
	}

	public boolean isTimePeriodAnnually() {
		return timePeriodAnnually;
	}

	public void setTimePeriodAnnually(boolean timePeriodAnnually) {
		this.timePeriodAnnually = timePeriodAnnually;
	}

	public YearMonth getYearStart() {
		return yearStart;
	}

	public void setYearStart(YearMonth yearStart) {
		this.yearStart = yearStart;
	}

	public YearMonth getYearEnd() {
		return yearEnd;
	}

	public void setYearEnd(YearMonth yearEnd) {
		this.yearEnd = yearEnd;
	}

	public String getDisplayLevel() {
		return displayLevel;
	}

	public void setDisplayLevel(String displayLevel) {
		this.displayLevel = displayLevel;
	} 

	public List<String> getYearList() {
		return yearList;
	}
	
	public void addYearList(String year) {
		this.yearList.add(year);
	}
	
	public void setYearList(List<String> yearList) {
		this.yearList = yearList;
	}
}
