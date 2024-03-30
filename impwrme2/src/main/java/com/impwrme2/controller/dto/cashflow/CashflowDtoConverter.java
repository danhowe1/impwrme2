package com.impwrme2.controller.dto.cashflow;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import com.impwrme2.controller.dto.cashflowDateRangeValue.CashflowDateRangeValueDto;
import com.impwrme2.controller.dto.cashflowDateRangeValue.CashflowDateRangeValueDtoConverter;
import com.impwrme2.model.cashflow.Cashflow;
import com.impwrme2.model.cashflow.CashflowCategory;
import com.impwrme2.model.cashflow.CashflowFrequency;
import com.impwrme2.model.cashflowDateRangeValue.CashflowDateRangeValue;
import com.impwrme2.model.resource.Resource;
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
		cashflowDto.setCategory(cashflow.getCategory().getValue());
		cashflowDto.setCategoryMessage(messageSource.getMessage(cashflow.getCategory().getMessageCode(), null, LocaleContextHolder.getLocale()));
		cashflowDto.setDetail(cashflow.getDetail());
		cashflowDto.setFrequency(cashflow.getFrequency().getValue());
		cashflowDto.setCpiAffected(cashflow.getCpiAffected());
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
					cashflowDto.addCashflowDateRangeValueDto(cfdrvDto);
				}
			}
			cashflowTableDto.addCashflowDto(cashflowDto);
		}
		return cashflowTableDto;
	}

	public void dtoToExistingEntity(CashflowDto cashflowDto, Cashflow cashflow) {
		cashflow.setCategory(CashflowCategory.getCategory(cashflowDto.getCategory()));
		cashflow.setDetail(cashflowDto.getDetail());
		cashflow.setFrequency(CashflowFrequency.getFrequency(cashflowDto.getFrequency()));
		cashflow.setCpiAffected(cashflowDto.getCpiAffected());
	};
	
	public Cashflow dtoToNewEntity(CashflowCreateDto cashflowCreateDto, final Resource resource) {
		Cashflow cashflow = new Cashflow(CashflowCategory.getCategory(cashflowCreateDto.getCategory()), cashflowCreateDto.getDetail(), CashflowFrequency.getFrequency(cashflowCreateDto.getFrequency()), cashflowCreateDto.getCpiAffected());
		CashflowDateRangeValue cfdrv = new CashflowDateRangeValue(cashflow.getCategory().getType(), YearMonthUtils.getYearMonthFromStringInFormatMM_YYYY(cashflowCreateDto.getYearMonthStart()), YearMonthUtils.getYearMonthFromStringInFormatMM_YYYY(cashflowCreateDto.getYearMonthEnd()), cashflowCreateDto.getValue());
		cashflow.addCashflowDateRangeValue(cfdrv);
		resource.addCashflow(cashflow);
		return cashflow;
	}
}
