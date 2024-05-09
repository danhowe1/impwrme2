package com.impwrme2.controller.dto.resource;

import java.util.ArrayList;
import java.util.List;

import com.impwrme2.model.resource.Resource;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ResourceCreateDto {

	@NotNull(message = "Please provide a scenario ID.")
	private Long scenarioId;
	
	@NotEmpty(message = "Please provide a resource name.")
	private String name;

	@NotEmpty(message = "Please provide a start date.")
	@Pattern(regexp = "^[0-9]{1,2}.[0-9]{4}", message = "Start date must be in format MM YYYY")
	private String startYearMonth;

	@NotEmpty(message = "Please provide a resource type.")
	private String resourceType;

	private Long parentId;
		
	private List<Resource> listOfAllowedParentResources = new ArrayList<Resource>();
	
	@Valid
	private List<ResourceCreateResourceParamWithValueDto> resourceParamDtos = new ArrayList<ResourceCreateResourceParamWithValueDto>();

	//-------------------
	// Getters & setters.
	//-------------------
	
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

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public List<Resource> getListOfAllowedParentResources() {
		return listOfAllowedParentResources;
	}

	public void setListOfAllowedParentResources(List<Resource> listOfAllowedParentResources) {
		this.listOfAllowedParentResources = listOfAllowedParentResources;
	}

	public List<ResourceCreateResourceParamWithValueDto> getResourceParamDtos() {
		return resourceParamDtos;
	}

//	public void setResourceParamDtos(List<ResourceCreateResourceParamWithValueDto> resourceParamDtos) {
//		this.resourceParamDtos = resourceParamDtos;
//	}
	
	public void addResourceParamDto(ResourceCreateResourceParamWithValueDto resourceParamDto) {
		this.resourceParamDtos.add(resourceParamDto);
	}
}
