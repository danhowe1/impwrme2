package com.impwrme2.controller.dto.resource;

import com.impwrme2.controller.dto.DtoUtils;
import com.impwrme2.model.resourceParam.ResourceParamType;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class ResourceCreateResourceParamWithValueDto {

	@NotEmpty(message = "{msg.validation.resourceParam.name.notEmpty}")
	private String name;

	@NotEmpty(message = "{msg.validation.notEmpty}")
	private String resourceParamType;

	@NotNull(message = "{msg.validation.resourceParam.userAbleToCreateNewDateValue.notNull}")
	private boolean userAbleToCreateNewDateValue;

	@NotEmpty(message = "{msg.validation.resourceParamDateValue.value.notEmpty}")
    private String value = "";
	
	//-------------------
	// Getters & setters.
	//-------------------

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getResourceParamType() {
		return resourceParamType;
	}

	public void setResourceParamType(String resourceParamType) {
		this.resourceParamType = resourceParamType;
	}

	public boolean isUserAbleToCreateNewDateValue() {
		return userAbleToCreateNewDateValue;
	}

	public void setUserAbleToCreateNewDateValue(boolean userAbleToCreateNewDateValue) {
		this.userAbleToCreateNewDateValue = userAbleToCreateNewDateValue;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getRegex() {
		return DtoUtils.resourceParamValueRegex(ResourceParamType.getResourceParamType(resourceParamType));
	}
}
