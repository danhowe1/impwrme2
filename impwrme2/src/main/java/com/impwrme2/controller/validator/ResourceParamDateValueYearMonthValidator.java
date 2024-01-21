package com.impwrme2.controller.validator;

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
		YearMonth rpdvDtoYearMonth = YearMonthUtils.getYearMonthFromStringInFormatMM_YYYY(rpdvDto.getYearMonth());
		ResourceParam<?> resourceParam = resourceParamService.findById(rpdvDto.getResourceParamId()).get();
		YearMonth resouceYearMonth = resourceParam.getResource().getStartYearMonth();
		if (rpdvDtoYearMonth.isBefore(resouceYearMonth)) {
		    context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("{msg.validation.resourceParamDateValue.yearMonth.notBeforeResourceStartDate}").addConstraintViolation();
			return false;
		} else if (!rpdvDtoYearMonth.equals(resouceYearMonth) && !rpdvDto.isUserAbleToChangeDate()) {
		    context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("{msg.validation.resourceParamDateValue.yearMonth.notAllowedToChangeDate}").addConstraintViolation();
			return false;			
		} else if (null == rpdvDto.getId() && !resourceParam.isUserAbleToCreateNewDateValue()) {
		    context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("{msg.validation.resourceParamDateValue.yearMonth.notAllowedToCreateDateValue}").addConstraintViolation();			
			return false;			
		}
		return true;
	}
}
