package com.impwrme2.controller.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.impwrme2.model.resource.Resource;
import com.impwrme2.model.resource.ResourceScenario;
import com.impwrme2.model.resource.ResourceType;

public class ResourceDropdownDto {

	public ResourceDropdownDto(final ResourceScenario scenario, final ResourceType activeResourceType) {
		this.activeResourceType = activeResourceType;
		initialise(scenario);
	}

	private final static String RESOURCE_NAV_LABEL_SCENARIO = "resourceNavLabelScenario";
	private final static String RESOURCE_NAV_LABEL_FAMILY = "resourceNavLabelFamily";

	private final ResourceType activeResourceType;
	private Long scenarioResourceId;

	private Map<String, ResourceDropdownTabDto> resourceTabMap = new HashMap<String, ResourceDropdownTabDto>();

	private void initialise(final ResourceScenario scenario) {

		scenarioResourceId = scenario.getId();
		resourceTabMap.put(RESOURCE_NAV_LABEL_FAMILY, new ResourceDropdownTabDto(RESOURCE_NAV_LABEL_FAMILY));

		for (Resource resource : scenario.getChildren()) {
			addItemToTab(getResourceNavLabel(resource.getResourceType()), resource);
		}
		
		for (ResourceDropdownTabDto tab : resourceTabMap.values()) {
			Collections.sort(tab.getResourceTabItems());			
		}
	}

	private String getResourceNavLabel(ResourceType resourceType) {
		
		switch (resourceType) {
		case HOUSEHOLD:
			return RESOURCE_NAV_LABEL_FAMILY;
		case PERSON:
			return RESOURCE_NAV_LABEL_FAMILY;
		case SCENARIO:
			return RESOURCE_NAV_LABEL_SCENARIO;
		default:
			throw new IllegalStateException("Unknown resource tpye " + resourceType.getValue() + ".");
		}
	}
	
	private void addItemToTab(final String resourceNavLabel, final Resource resource) {
		ResourceDropdownTabDto tab = resourceTabMap.get(resourceNavLabel);
		if (null == tab) {
			tab = new ResourceDropdownTabDto(resourceNavLabel);
			resourceTabMap.put(resourceNavLabel, tab);
		}
		tab.addResourceTabItem(new ResourceDropdownTabItemDto(resource));
		Collections.sort(tab.getResourceTabItems());
	}

	// -------------------
	// Getters & setters.
	// -------------------

	public String getScenarioTabLabel() {
		return RESOURCE_NAV_LABEL_SCENARIO;
	}
	
	public Long getScenarioResourceId() {
		return scenarioResourceId;
	}
	
	public List<ResourceDropdownTabDto> getResourceTabs() {
		return new ArrayList<ResourceDropdownTabDto>(resourceTabMap.values());
	}

//	public void setResourceTabs(List<ResourceDropdownTabDto> resourceTabs) {
//		this.resourceTabs = resourceTabs;
//	}
//
//	public void addResourceTab(ResourceDropdownTabDto resourceTab) {
//		this.resourceTabs.add(resourceTab);
//	}
	
	public String getActiveResourceNavLabel() {
		return getResourceNavLabel(activeResourceType);
	}
}