package com.impwrme2.controller.dto.resourceParam;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.impwrme2.controller.dto.resourceParamDateValue.ResourceParamDateValueDto;
import com.impwrme2.controller.dto.resourceParamDateValue.ResourceParamDateValueDtoConverter;
import com.impwrme2.model.resourceParam.ResourceParam;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValue;
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
		resourceParamDto.setRequestParamType(resourceParam.getResourceParamType().getValue());
		return resourceParamDto;
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
					rpdvDto.setResourceParamId(resourceParam.getId());
					rpdvDto.setResourceParamType(resourceParam.getResourceParamType().getValue());
					resourceParamDto.addResourceParamDateValueDto(rpdvDto);
				}
			}
			resourceParamTableDto.addResourceParamDto(resourceParamDto);
		}
		return resourceParamTableDto;
	}
}