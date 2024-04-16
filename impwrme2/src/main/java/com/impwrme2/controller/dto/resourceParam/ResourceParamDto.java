package com.impwrme2.controller.dto.resourceParam;

import java.util.ArrayList;
import java.util.List;

import com.impwrme2.controller.dto.resourceParamDateValue.ResourceParamDateValueDto;
import com.impwrme2.controller.dto.resourceParamDateValue.ValueMessagePairDto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class ResourceParamDto {

	private Long id;

	@NotEmpty(message = "{msg.validation.resourceParam.name.notEmpty}")
	private String name;

	@NotNull(message = "{msg.validation.resourceParam.userAbleToCreateNewDateValue.notNull}")
	private boolean userAbleToCreateNewDateValue = false;
	
	@NotEmpty(message = "{msg.validation.notEmpty}")
	private String resourceParamType;
	
	private List<ResourceParamDateValueDto> resourceParamDateValueDtos = new ArrayList<ResourceParamDateValueDto>();
	
	private List<ValueMessagePairDto> listOfValueMessagePairs = new ArrayList<ValueMessagePairDto>();
	
	//-------------------
	// Getters & setters.
	//-------------------

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isUserAbleToCreateNewDateValue() {
		return userAbleToCreateNewDateValue;
	}

	public void setUserAbleToCreateNewDateValue(boolean userAbleToCreateNewDateValue) {
		this.userAbleToCreateNewDateValue = userAbleToCreateNewDateValue;
	}
	
	public String getResourceParamType() {
		return resourceParamType;
	}

	public void setResourceParamType(String resourceParamType) {
		this.resourceParamType = resourceParamType;
	}

	public List<ResourceParamDateValueDto> getResourceParamDateValueDtos() {
		return resourceParamDateValueDtos;
	}
	
	public void addResourceParamDateValueDto(ResourceParamDateValueDto rpdvDto) {
		resourceParamDateValueDtos.add(rpdvDto);	
	}

	public List<ValueMessagePairDto> getListOfValueMessagePairs() {
		return listOfValueMessagePairs;
	}

	public void addValueMessagePair(final ValueMessagePairDto valueMessagePairDto) {
		this.listOfValueMessagePairs.add(valueMessagePairDto);
	}
}
