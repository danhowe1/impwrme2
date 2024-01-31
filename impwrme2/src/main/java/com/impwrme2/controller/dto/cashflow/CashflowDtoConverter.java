package com.impwrme2.controller.dto.cashflow;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import com.impwrme2.controller.dto.cashflowDateRangeValue.CashflowDateRangeValueDto;
import com.impwrme2.controller.dto.cashflowDateRangeValue.CashflowDateRangeValueDtoConverter;
import com.impwrme2.model.cashflow.Cashflow;
import com.impwrme2.model.cashflowDateRangeValue.CashflowDateRangeValue;
import com.impwrme2.utils.YearMonthUtils;

@Component
public class CashflowDtoConverter {

	@Autowired
	CashflowDateRangeValueDtoConverter cashflowDateRangeValueDtoConverter;

	@Autowired
	MessageSource messageSource;
	
	public CashflowDto entityToDto(Cashflow cashflow) {
		CashflowDto cashflowDto = new CashflowDto();
		cashflowDto.setId(cashflow.getId());
		cashflowDto.setCategoryMessage(messageSource.getMessage("msg.class.cashflowCategory." + cashflow.getCategory().getValue(), null, LocaleContextHolder.getLocale()));
		cashflowDto.setDetail(cashflow.getDetail());
		cashflowDto.setFrequency(cashflow.getFrequency().getValue());
		cashflowDto.setCpiAffected(cashflow.getCpiAffected());
		cashflowDto.setType(cashflow.getType().getValue());
		return cashflowDto;
	}

	public CashflowTableDto cashflowsToCashflowTableDto(List<Cashflow> cashflows) {
		CashflowTableDto cashflowTableDto = new CashflowTableDto(cashflows);
		for (Cashflow cashflow : cashflows) {
			CashflowDto cashflowDto = entityToDto(cashflow);

			for (int i = 0; i < cashflowTableDto.getDates().size(); i++) {
				String headerDateStr = cashflowTableDto.getDates().get(i);
				boolean dateMatchFound = false;
				for (CashflowDateRangeValue cfdrv : cashflow.getCashflowDateRangeValues()) {
					if (headerDateStr.equals(YearMonthUtils.getStringInFormatMM_YYYYFromYearMonth(cfdrv.getYearMonthStart())) ||
						headerDateStr.equals(YearMonthUtils.getStringInFormatMM_YYYYFromYearMonth(cfdrv.getYearMonthEnd()))) {
						dateMatchFound = true;
						CashflowDateRangeValueDto cfdrvDto = cashflowDateRangeValueDtoConverter.entityToDto(cfdrv);
						if (headerDateStr.equals(YearMonthUtils.getStringInFormatMM_YYYYFromYearMonth(cfdrv.getYearMonthEnd()))) {
							cfdrvDto.setValue(Integer.valueOf(0));
						}
						cashflowDto.addCashflowDateRangeValueDto(cfdrvDto);
						break;
					}
				}
				if (!dateMatchFound) {
					CashflowDateRangeValueDto cfdrvDto = new CashflowDateRangeValueDto();
					cfdrvDto.setYearMonthStart(headerDateStr);
					cfdrvDto.setCashflowId(cashflow.getId());
					cfdrvDto.setCashflowType(cashflow.getType().getValue());
					cashflowDto.addCashflowDateRangeValueDto(cfdrvDto);
				}
			}
			cashflowTableDto.addCashflowDto(cashflowDto);
		}
		return cashflowTableDto;
	}
}
