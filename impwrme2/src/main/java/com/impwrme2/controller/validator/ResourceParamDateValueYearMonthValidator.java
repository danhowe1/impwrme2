package com.impwrme2.controller.validator;

import java.time.DateTimeException;
import java.time.YearMonth;

import org.springframework.beans.factory.annotation.Autowired;

import com.impwrme2.controller.dto.resourceParamDateValue.ResourceParamDateValueDto;
import com.impwrme2.model.resourceParam.ResourceParam;
import com.impwrme2.service.resourceParam.ResourceParamService;
import com.impwrme2.utils.YearMonthUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ResourceParamDateValueYearMonthValidator implements ConstraintValidator<ResourceParamDateValueYearMonthConstraint, ResourceParamDateValueDto> {

	@Autowired
	private ResourceParamService resourceParamService;

	@Override
	public boolean isValid(ResourceParamDateValueDto rpdvDto, ConstraintValidatorContext context) {
		YearMonth rpdvDtoYearMonth;
		try {
			rpdvDtoYearMonth = YearMonthUtils.getYearMonthFromStringInFormatMM_YYYY(rpdvDto.getYearMonth());
		} catch (DateTimeException e) {
			return getErrorMessageAndReturn(context, "{msg.validation.monthYear.invalidDate}");
		}
		ResourceParam<?> resourceParam = resourceParamService.findById(rpdvDto.getResourceParamId()).get();
		YearMonth resouceYearMonth = resourceParam.getResource().getStartYearMonth();
		if (rpdvDtoYearMonth.isBefore(resouceYearMonth)) {
			return getErrorMessageAndReturn(context, "{msg.validation.resourceParamDateValue.yearMonth.notBeforeResourceStartDate}");
		} else if (!rpdvDtoYearMonth.equals(resouceYearMonth) && !rpdvDto.isUserAbleToChangeDate()) {
			return getErrorMessageAndReturn(context, "{msg.validation.resourceParamDateValue.yearMonth.notAllowedToChangeDate}");
		} else if (null == rpdvDto.getId() && !resourceParam.isUserAbleToCreateNewDateValue()) {
			return getErrorMessageAndReturn(context, "{msg.validation.resourceParamDateValue.yearMonth.notAllowedToCreateDateValue}");
		}
		return true;
	}
	
	private boolean getErrorMessageAndReturn(ConstraintValidatorContext context, String messageCode) {
	    context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(messageCode).addConstraintViolation();			
		return false;
	}
}
