package com.impwrme2.controller.dto.resource;

import org.springframework.stereotype.Component;

import com.impwrme2.model.resource.Resource;
import com.impwrme2.utils.YearMonthUtils;

@Component
public class ResourceDtoConverter {

	public ResourceDto entityToDto(Resource resource) {
		ResourceDto resourceDto = new ResourceDto();
		resourceDto.setId(resource.getId());
		resourceDto.setName(resource.getName());
		resourceDto.setStartYearMonth(YearMonthUtils.getStringInFormatMM_YYYYFromYearMonth(resource.getStartYearMonth()));
		resourceDto.setResourceType(resource.getResourceType().getValue());
		return resourceDto;
	}
}
