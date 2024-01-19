package com.impwrme2.controller.dto.resourceParamDateValue;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ResourceParamDateValueDto {

	private Long id;

	@NotEmpty(message = "*Please provide a date.")
	@Pattern(regexp = "^[0-9]{1,2}.[0-9]{4}", message = "Date must be in format MM YYYY")
	private String yearMonth;

	@NotNull(message = "*Please provide a param value.")
    private String value = "";

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
}
