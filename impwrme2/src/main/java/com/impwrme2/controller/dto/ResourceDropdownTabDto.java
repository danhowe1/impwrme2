package com.impwrme2.controller.dto;

import java.util.ArrayList;
import java.util.List;

public class ResourceDropdownTabDto {

	private String resourceTabLabel;

	private List<ResourceDropdownTabItemDto> resourceTabItems = new ArrayList<ResourceDropdownTabItemDto>();

	public String getResourceTabLabel() {
		return resourceTabLabel;
	}

	public void setResourceTabLabel(String resourceTabLabel) {
		this.resourceTabLabel = resourceTabLabel;
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
