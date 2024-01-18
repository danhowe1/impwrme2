package com.impwrme2.controller.dto.resourceParamDateValue;

import org.springframework.stereotype.Component;

import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValue;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueBigDecimal;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueInteger;
import com.impwrme2.utils.YearMonthUtils;

@Component
public class ResourceParamDateValueDtoConverter {

	public ResourceParamDateValueDto entityToDto(ResourceParamDateValue<?> rpdv) {
		ResourceParamDateValueDto rpdvDto = new ResourceParamDateValueDto();
		rpdvDto.setId(rpdv.getId());
		rpdvDto.setYearMonth(YearMonthUtils.getStringInFormatMM_YYYYFromYearMonth(rpdv.getYearMonth()));
		if (rpdv instanceof ResourceParamDateValueBigDecimal) {
			rpdvDto.setValue(String.valueOf(rpdv.getValue()));
		} else if (rpdv instanceof ResourceParamDateValueInteger) {
			rpdvDto.setValue(String.valueOf(rpdv.getValue()));
		} else {
			throw new IllegalStateException("Unknown resource parameter for id " + String.valueOf(rpdv.getId()) + ".");
		}
		return rpdvDto;
	}	
}
