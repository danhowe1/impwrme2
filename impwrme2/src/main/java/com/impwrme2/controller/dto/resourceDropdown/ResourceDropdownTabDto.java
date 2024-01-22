package com.impwrme2.controller.dto.resourceDropdown;

import java.util.ArrayList;
import java.util.List;

public class ResourceDropdownTabDto {

	ResourceDropdownTabDto(final String resourceTabLabel) {
		this.resourceTabLabel = resourceTabLabel;
	}
	private final String resourceTabLabel;

	private List<ResourceDropdownTabItemDto> resourceTabItems = new ArrayList<ResourceDropdownTabItemDto>();

	//-------------------
	// Getters & setters.
	//-------------------
	
	public String getResourceTabLabel() {
		return resourceTabLabel;
	}

	public List<ResourceDropdownTabItemDto> getResourceTabItems() {
		return resourceTabItems;
	}

	public void setResourceTabItems(List<ResourceDropdownTabItemDto> resourceTabItems) {
		this.resourceTabItems = resourceTabItems;
	}

	public void addResourceTabItem(ResourceDropdownTabItemDto resourceTabItem) {
		this.resourceTabItems.add(resourceTabItem);
	}
}
