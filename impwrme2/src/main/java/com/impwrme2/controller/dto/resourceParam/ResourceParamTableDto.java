package com.impwrme2.controller.dto.resourceParam;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.impwrme2.model.resourceParam.ResourceParam;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValue;
import com.impwrme2.utils.YearMonthUtils;

public class ResourceParamTableDto {

	public ResourceParamTableDto(List<ResourceParam<?>> resourceParams) {
		initialise(resourceParams);
	}
	
	private List<String> dates = new ArrayList<String>();
	
	private List<ResourceParamDto> resourceParamDtos = new ArrayList<ResourceParamDto>();

	private void initialise(List<ResourceParam<?>> resourceParams) {
		SortedSet<YearMonth> rpdvDates = new TreeSet<YearMonth>();
		for (ResourceParam<?> resourceParam : resourceParams) {
			for (ResourceParamDateValue<?> rpdv : resourceParam.getResourceParamDateValues()) {
				rpdvDates.add(rpdv.getYearMonth());
			}
		}
		for (YearMonth yearMonth : rpdvDates) {
			dates.add(YearMonthUtils.getStringInFormatMM_YYYYFromYearMonth(yearMonth));
		}
	}
	
	//-------------------
	// Getters & setters.
	//-------------------

	public List<String> getDates() {
		return dates;
	}

	public List<ResourceParamDto> getResourceParamDtos() {
		return resourceParamDtos;
	}
	
	public void addResourceParamDto(ResourceParamDto resourceParamDto) {
		resourceParamDtos.add(resourceParamDto);
	}
}
