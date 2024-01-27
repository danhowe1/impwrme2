package com.impwrme2.controller.dto.cashflow;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.impwrme2.model.cashflow.Cashflow;
import com.impwrme2.model.cashflowDateRangeValue.CashflowDateRangeValue;
import com.impwrme2.utils.YearMonthUtils;

public class CashflowTableDto {

	public CashflowTableDto(List<Cashflow> cashflows) {
		initialise(cashflows);
	}
	
	private List<String> dates = new ArrayList<String>();
	
	private List<CashflowDto> cashflowDtos = new ArrayList<CashflowDto>();

	private void initialise(List<Cashflow> cashflows) {
		SortedSet<YearMonth> cfdrvDates = new TreeSet<YearMonth>();
		for (Cashflow cashflow : cashflows) {
			for (CashflowDateRangeValue cfdrv : cashflow.getCashflowDateRangeValues()) {
				cfdrvDates.add(cfdrv.getYearMonthStart());
				if (null != cfdrv.getYearMonthEnd()) {					
					cfdrvDates.add(cfdrv.getYearMonthEnd());
				}
			}
		}
		for (YearMonth yearMonth : cfdrvDates) {
			dates.add(YearMonthUtils.getStringInFormatMM_YYYYFromYearMonth(yearMonth));
		}
	}
	
	//-------------------
	// Getters & setters.
	//-------------------

	public List<String> getDates() {
		return dates;
	}

	public List<CashflowDto> getCashflowDtos() {
		return cashflowDtos;
	}
	
	public void addCashflowDto(CashflowDto cashflowDto) {
		cashflowDtos.add(cashflowDto);
	}
}
