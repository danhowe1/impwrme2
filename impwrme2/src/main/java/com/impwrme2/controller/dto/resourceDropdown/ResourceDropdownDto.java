package com.impwrme2.controller.dto.resourceDropdown;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.impwrme2.model.resource.Resource;
import com.impwrme2.model.resource.ResourceScenario;
import com.impwrme2.model.resource.ResourceType;
import com.impwrme2.model.scenario.Scenario;

public class ResourceDropdownDto {

	public ResourceDropdownDto(final Scenario scenario, final ResourceType activeResourceType) {
		this.activeResourceType = activeResourceType;
		initialise(scenario);
	}

	private final static String RESOURCE_NAV_LABEL_SCENARIO = "resourceNavLabelScenario";
	private final static String RESOURCE_NAV_LABEL_FAMILY = "resourceNavLabelFamily";
	private final static String RESOURCE_NAV_LABEL_LOANS = "resourceNavLabelLoans";
	private final static String RESOURCE_NAV_LABEL_SAVINGS = "resourceNavLabelSavings";

	private final ResourceType activeResourceType;
	private Long scenarioResourceId;

	private Map<String, ResourceDropdownTabDto> resourceTabMap = new HashMap<String, ResourceDropdownTabDto>();

	private void initialise(final Scenario scenario) {

		scenarioResourceId = scenario.getResourceScenario().getId();
		resourceTabMap.put(RESOURCE_NAV_LABEL_FAMILY, new ResourceDropdownTabDto(RESOURCE_NAV_LABEL_FAMILY));

		for (Resource resource : scenario.getResources()) {
			if (!(resource instanceof ResourceScenario)) {
				addItemToTab(getResourceNavLabel(resource.getResourceType()), resource);
			}
		}
		
//		for (ResourceDropdownTabDto tab : resourceTabMap.values()) {
//			Collections.sort(tab.getResourceTabItems());			
//		}
	}

	private String getResourceNavLabel(ResourceType resourceType) {
		
		switch (resourceType) {
		case CREDIT_CARD:
			return RESOURCE_NAV_LABEL_LOANS;
		case CURRENT_ACCOUNT:
			return RESOURCE_NAV_LABEL_SAVINGS;
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
//		Collections.sort(tab.getResourceTabItems());
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
	
	public String getActiveResourceNavLabel() {
		return getResourceNavLabel(activeResourceType);
	}
}
