package com.impwrme2.controller.dto;

public class ResourceDropdownTabItemDto {

	ResourceDropdownTabItemDto(final String resourceItemLabel, final Long resourceId) {
		this.resourceItemLabel = resourceItemLabel;
		this.resourceId = resourceId;
	}
	
	private String resourceItemLabel;
	private Long resourceId;

	//-------------------
	// Getters & setters.
	//-------------------
	
	public String getResourceItemLabel() {
		return resourceItemLabel;
	}

	public Long getResourceId() {
		return resourceId;
	}
}
