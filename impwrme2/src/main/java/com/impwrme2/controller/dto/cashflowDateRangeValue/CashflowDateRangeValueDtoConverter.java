package com.impwrme2.controller.dto.cashflowDateRangeValue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.impwrme2.model.cashflow.Cashflow;
import com.impwrme2.model.cashflowDateRangeValue.CashflowDateRangeValue;
import com.impwrme2.service.cashflow.CashflowService;
import com.impwrme2.service.cashflowDateRangeValue.CashflowDateRangeValueService;
import com.impwrme2.utils.YearMonthUtils;

@Component
public class CashflowDateRangeValueDtoConverter {

	@Autowired
	private CashflowService cashflowService;

	@Autowired
	private CashflowDateRangeValueService cashflowDateRangeValueService;

	public CashflowDateRangeValueDto entityToDto(CashflowDateRangeValue cfdrv) {
		CashflowDateRangeValueDto cfdrvDto = new CashflowDateRangeValueDto();
		cfdrvDto.setId(cfdrv.getId());
		cfdrvDto.setYearMonthStart(YearMonthUtils.getStringInFormatMM_YYYYFromYearMonth(cfdrv.getYearMonthStart()));
		cfdrvDto.setYearMonthEnd(YearMonthUtils.getStringInFormatMM_YYYYFromYearMonth(cfdrv.getYearMonthEnd()));
		cfdrvDto.setCashflowId(cfdrv.getCashflow().getId());
		cfdrvDto.setValue(cfdrv.getValue());
		return cfdrvDto;
	}

	public CashflowDateRangeValue dtoToEntity(CashflowDateRangeValueDto cfdrvDto) {
		CashflowDateRangeValue cfdrv;
		if (null != cfdrvDto.getId()) {
			cfdrv = cashflowDateRangeValueService.findById(cfdrvDto.getId()).get();
			cfdrv.setYearMonthStart(YearMonthUtils.getYearMonthFromStringInFormatMM_YYYY(cfdrvDto.getYearMonthStart()));
			cfdrv.setYearMonthEnd(YearMonthUtils.getYearMonthFromStringInFormatMM_YYYY(cfdrvDto.getYearMonthEnd()));			
			cfdrv.setValue(cfdrvDto.getValue());
		} else {
			cfdrv = new CashflowDateRangeValue(YearMonthUtils.getYearMonthFromStringInFormatMM_YYYY(cfdrvDto.getYearMonthStart()), YearMonthUtils.getYearMonthFromStringInFormatMM_YYYY(cfdrvDto.getYearMonthEnd()), cfdrvDto.getValue());
			Cashflow cashflow = cashflowService.findById(cfdrvDto.getCashflowId()).get();
			cashflow.addCashflowDateRangeValue(cfdrv);
		}
		return cfdrv;
	}
}
