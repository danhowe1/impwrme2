package com.impwrme2.controller.validator;

import java.time.DateTimeException;
import java.time.YearMonth;

import org.springframework.beans.factory.annotation.Autowired;

import com.impwrme2.controller.dto.cashflowDateRangeValue.CashflowDateRangeValueDto;
import com.impwrme2.model.cashflow.Cashflow;
import com.impwrme2.service.cashflow.CashflowService;
import com.impwrme2.utils.YearMonthUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CashflowDateRangeValueYearMonthValidator implements ConstraintValidator<CashflowDateRangeValueYearMonthConstraint, CashflowDateRangeValueDto> {

	@Autowired
	private CashflowService cashflowService;

	@Override
	public boolean isValid(CashflowDateRangeValueDto cfdrvDto, ConstraintValidatorContext context) {
		YearMonth cfdrvDtoYearMonthStart;
		try {
			cfdrvDtoYearMonthStart = YearMonthUtils.getYearMonthFromStringInFormatMM_YYYY(cfdrvDto.getYearMonthStart());
		} catch (DateTimeException e) {
			return getErrorMessageAndReturn(context, "{msg.validation.monthYear.invalidDate}");
		}

		Cashflow cashflow = cashflowService.findById(cfdrvDto.getCashflowId()).get();
		YearMonth resouceYearMonth = cashflow.getResource().getStartYearMonth();
		if (cfdrvDtoYearMonthStart.isBefore(resouceYearMonth)) {
			return getErrorMessageAndReturn(context, "{msg.validation.cashflowDateRangeValue.yearMonthStart.notBeforeResourceStartDate}");
		}
		
		if (null != cfdrvDto.getYearMonthEnd()) {
			YearMonth cfdrvDtoYearMonthEnd;
			try {
				cfdrvDtoYearMonthEnd = YearMonthUtils.getYearMonthFromStringInFormatMM_YYYY(cfdrvDto.getYearMonthEnd());
			} catch (DateTimeException e) {
				return getErrorMessageAndReturn(context, "{msg.validation.monthYear.invalidDate}");
			}
			
			if (null != cfdrvDtoYearMonthEnd && !cfdrvDtoYearMonthEnd.isAfter(cfdrvDtoYearMonthStart)) {
				return getErrorMessageAndReturn(context, "{msg.validation.cashflowDateRangeValue.yearMonthEnd.notBeforeYearMonthStart}");				
			}
		}
		return true;
	}
	
	private boolean getErrorMessageAndReturn(ConstraintValidatorContext context, String messageCode) {
	    context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(messageCode).addConstraintViolation();			
		return false;
	}
}
