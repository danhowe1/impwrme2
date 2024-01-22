package com.impwrme2.controller.dto.resourceDropdown;

import com.impwrme2.model.resource.Resource;
import com.impwrme2.model.resource.ResourceType;

public class ResourceDropdownTabItemDto implements Comparable<ResourceDropdownTabItemDto> {

	ResourceDropdownTabItemDto(final Resource resource) {
		this.resourceId = resource.getId();
		this.resourceItemLabel = resource.getName();
		this.resourceType = resource.getResourceType().getValue();
	}
	
	private final Long resourceId;
	private final String resourceItemLabel;
	private final String resourceType;

	@Override
	public int compareTo(ResourceDropdownTabItemDto o) {
		
		if (this.getResourceType().equals(ResourceType.HOUSEHOLD.getValue())) {
			return -1;
		} else if (o.getResourceType().equals(ResourceType.HOUSEHOLD.getValue())) {
			return 1;
		}
		
		int compareName = this.getResourceItemLabel().compareTo(o.getResourceItemLabel());
		if (compareName != 0) {
			return compareName;
		}

		return 0;
	}

	//-------------------
	// Getters & setters.
	//-------------------

	public Long getResourceId() {
		return resourceId;
	}
	
	public String getResourceItemLabel() {
		return resourceItemLabel;
	}

	public String getResourceType() {
		return resourceType;
	}
}
