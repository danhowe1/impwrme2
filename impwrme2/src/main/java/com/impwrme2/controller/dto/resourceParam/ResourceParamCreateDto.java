package com.impwrme2.controller.dto.resourceParam;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ResourceParamCreateDto {

	@NotEmpty(message = "{msg.validation.resourceParam.name.notEmpty}")
	private String name;

	@NotEmpty(message = "{msg.validation.notEmpty}")
	private String resourceParamType;

	@NotNull(message = "{msg.validation.resourceParam.resourceId.notNull}")
	private Long resourceId;

	@NotEmpty(message = "{msg.validation.resourceParamDateValue.yearMonth.notNull}")
	@Pattern(regexp = "^[0-9]{1,2}.[0-9]{4}", message = "Date must be in format MM YYYY")
	private String yearMonth;

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

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	public String getYearMonth() {
		return yearMonth;
	}

	public void setYearMonth(String yearMonth) {
		this.yearMonth = yearMonth;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
