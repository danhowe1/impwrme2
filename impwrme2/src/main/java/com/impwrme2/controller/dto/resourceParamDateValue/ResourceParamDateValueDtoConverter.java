package com.impwrme2.controller.dto.resourceParamDateValue;

import java.time.YearMonth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import com.impwrme2.model.resourceParam.enums.ResourceParamStringValueEnum;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValue;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueBigDecimal;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueIntegerNegative;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueIntegerPositive;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueString;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueYearMonth;
import com.impwrme2.utils.YearMonthUtils;

@Component
public class ResourceParamDateValueDtoConverter {

	@Autowired
	MessageSource messageSource;

	public ResourceParamDateValueDto entityToDto(ResourceParamDateValue<?> rpdv) {
		ResourceParamDateValueDto rpdvDto = new ResourceParamDateValueDto();
		rpdvDto.setId(rpdv.getId());
		rpdvDto.setYearMonth(YearMonthUtils.getStringInFormatMM_YYYYFromYearMonth(rpdv.getYearMonth()));
		rpdvDto.setUserAbleToChangeDate(rpdv.isUserAbleToChangeDate());
		rpdvDto.setResourceParamId(rpdv.getResourceParam().getId());
		rpdvDto.setResourceParamType(rpdv.getResourceParam().getResourceParamType().getValue());
		if (rpdv instanceof ResourceParamDateValueBigDecimal) {
			rpdvDto.setValue(String.valueOf(rpdv.getValue()));
		} else if (rpdv instanceof ResourceParamDateValueIntegerNegative) {
			rpdvDto.setValue(String.valueOf(rpdv.getValue()));
		} else if (rpdv instanceof ResourceParamDateValueIntegerPositive) {
			rpdvDto.setValue(String.valueOf(rpdv.getValue()));
		} else if (rpdv instanceof ResourceParamDateValueString) {
			rpdvDto.setValue((String)rpdv.getValue());
			String messageCode = ResourceParamStringValueEnum.getMessageCode(rpdvDto.getValue());
			rpdvDto.setValueMessage(messageSource.getMessage(messageCode, null, LocaleContextHolder.getLocale()));
		} else if (rpdv instanceof ResourceParamDateValueYearMonth) {
			rpdvDto.setValue(YearMonthUtils.getStringInFormatMM_YYYYFromYearMonth((YearMonth)rpdv.getValue()));
		} else {
			throw new IllegalStateException("Unknown resource parameter for id " + String.valueOf(rpdv.getId()) + ".");
		}
		return rpdvDto;
	}
}
