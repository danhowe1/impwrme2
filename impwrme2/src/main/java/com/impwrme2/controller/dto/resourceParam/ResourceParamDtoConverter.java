package com.impwrme2.controller.dto.resourceParam;

import java.time.YearMonth;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.impwrme2.controller.dto.resourceParamDateValue.ResourceParamDateValueDto;
import com.impwrme2.controller.dto.resourceParamDateValue.ResourceParamDateValueDtoConverter;
import com.impwrme2.model.resourceParam.ResourceParam;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValue;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueBigDecimal;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueInteger;
import com.impwrme2.utils.YearMonthUtils;

@Component
public class ResourceParamDtoConverter {

	@Autowired
	private ResourceParamDateValueDtoConverter rpdvDtoConverter;

	public ResourceParamDto entityToDto(ResourceParam<?> resourceParam) {
		ResourceParamDto resourceParamDto = new ResourceParamDto();
		resourceParamDto.setId(resourceParam.getId());
		resourceParamDto.setName(resourceParam.getName());
		resourceParamDto.setUserAbleToCreateNewDateValue(resourceParam.isUserAbleToCreateNewDateValue());
		resourceParamDto.setResourceParamType(resourceParam.getResourceParamType().getValue());
		return resourceParamDto;
	}
	
	public ResourceParamDateValue<?> dtoToEntity(ResourceParamDateValueDto rpdvDto) {
		ResourceParamDateValue<?> rpdv = getResourceParamDateValue(rpdvDto);
		rpdv.setId(rpdvDto.getId());
		return rpdv;
	}

	public ResourceParamTableDto resourceParamsToResourceParamTableDto(List<ResourceParam<?>> resourceParams) {
		ResourceParamTableDto resourceParamTableDto = new ResourceParamTableDto(resourceParams);
		for (ResourceParam<?> resourceParam : resourceParams) {
			ResourceParamDto resourceParamDto = entityToDto(resourceParam);

			for (int i = 0; i < resourceParamTableDto.getDates().size(); i++) {
				String headerDateStr = resourceParamTableDto.getDates().get(i);
				boolean matchFound = false;
				for (ResourceParamDateValue<?> rpdv : resourceParam.getResourceParamDateValues()) {
					if (headerDateStr.equals(YearMonthUtils.getStringInFormatMM_YYYYFromYearMonth(rpdv.getYearMonth()))) {
						matchFound = true;
						ResourceParamDateValueDto rpdvDto = rpdvDtoConverter.entityToDto(rpdv);
						resourceParamDto.addResourceParamDateValueDto(rpdvDto);
						break;
					}
				}
				if (!matchFound) {
					ResourceParamDateValueDto rpdvDto = new ResourceParamDateValueDto();
					rpdvDto.setYearMonth(headerDateStr);
					rpdvDto.setUserAbleToChangeDate(true);
					rpdvDto.setResourceParamId(resourceParam.getId());
					rpdvDto.setResourceParamType(resourceParam.getResourceParamType().getValue());
					resourceParamDto.addResourceParamDateValueDto(rpdvDto);
				}
			}
			resourceParamTableDto.addResourceParamDto(resourceParamDto);
		}
		return resourceParamTableDto;
	}

	private ResourceParamDateValue<?> getResourceParamDateValue(ResourceParamDateValueDto rpdvDto) {
		YearMonth yearMonth = YearMonthUtils.getYearMonthFromStringInFormatMM_YYYY(rpdvDto.getYearMonth());
		switch (rpdvDto.getResourceParamType()) {
		case "BIG_DECIMAL":
			return new ResourceParamDateValueBigDecimal(yearMonth, rpdvDto.getValue());
		case "INTEGER":
			return new ResourceParamDateValueInteger(yearMonth, rpdvDto.getValue());
		default:
			throw new IllegalStateException("Unknown resource tpye " + rpdvDto.getResourceParamType() + ".");
		}
	}
}
