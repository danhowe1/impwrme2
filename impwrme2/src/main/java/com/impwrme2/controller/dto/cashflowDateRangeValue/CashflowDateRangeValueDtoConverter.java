package com.impwrme2.controller.dto.cashflowDateRangeValue;

import org.springframework.stereotype.Component;

import com.impwrme2.model.cashflowDateRangeValue.CashflowDateRangeValue;
import com.impwrme2.utils.YearMonthUtils;

@Component
public class CashflowDateRangeValueDtoConverter {

	public CashflowDateRangeValueDto entityToDto(CashflowDateRangeValue cfdrv) {
		CashflowDateRangeValueDto cfdrvDto = new CashflowDateRangeValueDto();
		cfdrvDto.setId(cfdrv.getId());
		cfdrvDto.setYearMonthStart(YearMonthUtils.getStringInFormatMM_YYYYFromYearMonth(cfdrv.getYearMonthStart()));
		cfdrvDto.setYearMonthEnd(YearMonthUtils.getStringInFormatMM_YYYYFromYearMonth(cfdrv.getYearMonthEnd()));
		cfdrvDto.setCashflowId(cfdrv.getCashflow().getId());
		cfdrvDto.setCashflowType(cfdrv.getCashflow().getCashflowType().getValue());
		cfdrvDto.setValue(cfdrv.getValue());
		return cfdrvDto;
	}	
}
