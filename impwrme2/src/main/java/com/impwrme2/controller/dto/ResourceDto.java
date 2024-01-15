package com.impwrme2.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ResourceDto {

	private Long id;
	
	@NotNull(message = "{msg.validation.resource.scenarioId.notNull}")
	private Long scenarioId;
	
	@NotEmpty(message = "{msg.validation.resource.name.notEmpty}")
	private String name;

	@NotEmpty(message = "{msg.validation.resource.startDate.notEmpty}")
	@Pattern(regexp = "^[0-9]{1,2}.[0-9]{4}", message = "Start date must be in format MM YYYY")
	private String startYearMonth;

	@NotEmpty(message = "{msg.validation.resource.resourceType.notEmpty}")
	private String resourceType;

	//-------------------
	// Getters & setters.
	//-------------------
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getScenarioId() {
		return scenarioId;
	}

	public void setScenarioId(Long scenarioId) {
		this.scenarioId = scenarioId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStartYearMonth() {
		return startYearMonth;
	}

	public void setStartYearMonth(String startYearMonth) {
		this.startYearMonth = startYearMonth;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

}
