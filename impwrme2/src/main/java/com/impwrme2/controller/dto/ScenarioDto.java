package com.impwrme2.controller.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public class ScenarioDto {

	private Long id;

	@NotEmpty(message = "{msg.validation.scenario.name.notEmpty}")
	private String name;

	private List<ResourceDto> resourceDtos = new ArrayList<ResourceDto>();

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

	public List<ResourceDto> getResourceDtos() {
		return resourceDtos;
	}

	public void setResourceDtos(List<ResourceDto> resourceDtos) {
		this.resourceDtos = resourceDtos;
	}

	public void addResourceDto(ResourceDto resourceDto) {
		this.resourceDtos.add(resourceDto);
	}
}
