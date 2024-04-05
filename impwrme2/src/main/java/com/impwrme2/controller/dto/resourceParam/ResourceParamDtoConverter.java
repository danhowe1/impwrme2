package com.impwrme2.controller.dto.resourceParam;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.impwrme2.controller.dto.resourceParamDateValue.ResourceParamDateValueDto;
import com.impwrme2.controller.dto.resourceParamDateValue.ResourceParamDateValueDtoConverter;
import com.impwrme2.model.resource.Resource;
import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParam.ResourceParam;
import com.impwrme2.model.resourceParam.ResourceParamBigDecimal;
import com.impwrme2.model.resourceParam.ResourceParamIntegerNegative;
import com.impwrme2.model.resourceParam.ResourceParamIntegerPositive;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValue;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueBigDecimal;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueIntegerNegative;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValueIntegerPositive;
import com.impwrme2.utils.YearMonthUtils;

import jakarta.validation.Valid;

@Component
public class ResourceParamDtoConverter {

	@Autowired
	private ResourceParamDateValueDtoConverter rpdvDtoConverter;

	public ResourceParamDto entityToDto(ResourceParam<?> resourceParam) {
		String resourceTypeStr = resourceParam.getResource().getResourceType().getValue();
		ResourceParamDto resourceParamDto = new ResourceParamDto();
		resourceParamDto.setId(resourceParam.getId());
		resourceParamDto.setName(resourceParam.getName().getMessageCode() + "." + resourceTypeStr);
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
			return new ResourceParamDateValueBigDecimal(yearMonth, rpdvDto.isUserAbleToChangeDate(), rpdvDto.getValue());
		case "INTEGER_NEGATIVE":
			return new ResourceParamDateValueIntegerNegative(yearMonth, rpdvDto.isUserAbleToChangeDate(), rpdvDto.getValue());
		case "INTEGER_POSITIVE":
			return new ResourceParamDateValueIntegerPositive(yearMonth, rpdvDto.isUserAbleToChangeDate(), rpdvDto.getValue());
		default:
			throw new IllegalStateException("Unknown resource tpye " + rpdvDto.getResourceParamType() + ".");
		}
	}

	public ResourceParam<?> dtoToNewEntity(@Valid ResourceParamCreateDto resourceParamCreateDto, Resource resource) {
		ResourceParam<?> resourceParam = getResourceParam(resourceParamCreateDto);
		resourceParam.setUserAbleToCreateNewDateValue(true);
		resource.addResourceParam(resourceParam);				
		return resourceParam;
	}

	private ResourceParam<?> getResourceParam(ResourceParamCreateDto resourceParamCreateDto) {
		YearMonth yearMonth = YearMonthUtils.getYearMonthFromStringInFormatMM_YYYY(resourceParamCreateDto.getYearMonth());
		switch (resourceParamCreateDto.getResourceParamType()) {
		case "BIG_DECIMAL":
			ResourceParamDateValueBigDecimal rpdvBD = new ResourceParamDateValueBigDecimal(yearMonth, true, new BigDecimal(resourceParamCreateDto.getValue()));
			ResourceParamBigDecimal resourceParamBD = new ResourceParamBigDecimal(ResourceParamNameEnum.getParamNameFromValue(resourceParamCreateDto.getName()));
			resourceParamBD.addResourceParamDateValue(rpdvBD);
			return resourceParamBD;
		case "INTEGER_NEGATIVE":
			ResourceParamDateValueIntegerNegative rpdvIN = new ResourceParamDateValueIntegerNegative(yearMonth, true, Integer.valueOf(resourceParamCreateDto.getValue()));
			ResourceParamIntegerNegative resourceParamIN = new ResourceParamIntegerNegative(ResourceParamNameEnum.getParamNameFromValue(resourceParamCreateDto.getName()));
			resourceParamIN.addResourceParamDateValue(rpdvIN);
			return resourceParamIN;
		case "INTEGER_POSITIVE":
			ResourceParamDateValueIntegerPositive rpdvIP = new ResourceParamDateValueIntegerPositive(yearMonth, true, Integer.valueOf(resourceParamCreateDto.getValue()));
			ResourceParamIntegerPositive resourceParamIP = new ResourceParamIntegerPositive(ResourceParamNameEnum.getParamNameFromValue(resourceParamCreateDto.getName()));
			resourceParamIP.addResourceParamDateValue(rpdvIP);
			return resourceParamIP;
		default:
			throw new IllegalStateException("Unknown resource param type " + resourceParamCreateDto.getResourceParamType() + ".");
		}
	}
}
