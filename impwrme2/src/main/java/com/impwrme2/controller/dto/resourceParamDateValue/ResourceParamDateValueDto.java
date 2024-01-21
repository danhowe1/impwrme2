package com.impwrme2.controller.dto.resourceParamDateValue;

import com.impwrme2.controller.validator.ResourceParamDateValueYearMonthConstraint;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@ResourceParamDateValueYearMonthConstraint
public class ResourceParamDateValueDto {

	private Long id;

	@NotEmpty(message = "{msg.validation.resourceParamDateValue.yearMonth.notNull}")
	@Pattern(regexp = "^[0-9]{1,2}.[0-9]{4}", message = "Date must be in format MM YYYY")
	private String yearMonth;

	@NotEmpty(message = "{msg.validation.resourceParamDateValue.value.notEmpty}")
    private String value = "";

	@NotNull(message = "{msg.validation.resourceParamDateValue.userAbleToChangeDate.notNull}")
	private boolean userAbleToChangeDate = false;

	@NotNull(message = "{msg.validation.resourceParamDateValue.resourceParam.notNull}")
	private Long resourceParamId;

	@NotEmpty(message = "{msg.validation.resourceParamDateValue.resourceType.notEmpty}")
	private String resourceParamType;
	
	//-------------------
	// Getters & setters.
	//-------------------

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public boolean isUserAbleToChangeDate() {
		return userAbleToChangeDate;
	}

	public void setUserAbleToChangeDate(boolean userAbleToChangeDate) {
		this.userAbleToChangeDate = userAbleToChangeDate;
	}

	public Long getResourceParamId() {
		return resourceParamId;
	}

	public void setResourceParamId(Long resourceParamId) {
		this.resourceParamId = resourceParamId;
	}

	public String getResourceParamType() {
		return resourceParamType;
	}

	public void setResourceParamType(String resourceParamType) {
		this.resourceParamType = resourceParamType;
	}
}
