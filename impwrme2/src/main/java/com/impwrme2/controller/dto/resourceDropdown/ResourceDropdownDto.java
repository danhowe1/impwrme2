package com.impwrme2.controller.dto.resourceDropdown;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.impwrme2.model.resource.Resource;
import com.impwrme2.model.resource.ResourcePropertyExisting;
import com.impwrme2.model.resource.ResourcePropertyNew;
import com.impwrme2.model.resource.ResourceScenario;
import com.impwrme2.model.resource.ResourceType;
import com.impwrme2.model.scenario.Scenario;

public class ResourceDropdownDto {

	public ResourceDropdownDto(final Scenario scenario, final ResourceType activeResourceType) {
		this.activeResourceType = activeResourceType;
		this.scenario = scenario;
		initialise(scenario);
	}

	private final static String RESOURCE_NAV_LABEL_SCENARIO = "resourceNavLabelScenario";
	private final static String RESOURCE_NAV_LABEL_FAMILY = "resourceNavLabelFamily";
	private final static String RESOURCE_NAV_LABEL_PROPERTY = "resourceNavLabelProperty";
	private final static String RESOURCE_NAV_LABEL_LOANS = "resourceNavLabelLoans";
	private final static String RESOURCE_NAV_LABEL_SAVINGS = "resourceNavLabelSavings";
	private final static String RESOURCE_NAV_LABEL_INVESTMENTS = "resourceNavLabelInvestments";

	private final ResourceType activeResourceType;
	private final Scenario scenario;

	private Map<String, ResourceDropdownTabDto> resourceTabMap = new HashMap<String, ResourceDropdownTabDto>();

	private void initialise(final Scenario scenario) {

		resourceTabMap.put(RESOURCE_NAV_LABEL_FAMILY, new ResourceDropdownTabDto(RESOURCE_NAV_LABEL_FAMILY));

		for (Resource resource : scenario.getResources()) {
			if (!(resource instanceof ResourceScenario)) {
				addItemToTab(getResourceNavLabel(resource.getResourceType()), resource);
			}
		}
		
		for (ResourceDropdownTabDto tab : resourceTabMap.values()) {
			Collections.sort(tab.getResourceTabItems());			
		}
	}

	private String getResourceNavLabel(ResourceType resourceType) {
		
		switch (resourceType) {
		case CREDIT_CARD:
			return RESOURCE_NAV_LABEL_LOANS;
		case CURRENT_ACCOUNT:
			return RESOURCE_NAV_LABEL_SAVINGS;
		case HOUSEHOLD:
			return RESOURCE_NAV_LABEL_FAMILY;
		case MORTGAGE:
			return RESOURCE_NAV_LABEL_LOANS;
		case MORTGAGE_OFFSET_ACCOUNT:
			return RESOURCE_NAV_LABEL_SAVINGS;
		case PERSON_ADULT:
			return RESOURCE_NAV_LABEL_FAMILY;
		case PERSON_CHILD:
			return RESOURCE_NAV_LABEL_FAMILY;
		case PROPERTY_EXISTING:
			return RESOURCE_NAV_LABEL_PROPERTY;
		case PROPERTY_NEW:
			return RESOURCE_NAV_LABEL_PROPERTY;
		case SAVINGS_ACCOUNT:
			return RESOURCE_NAV_LABEL_SAVINGS;
		case SCENARIO:
			return RESOURCE_NAV_LABEL_SCENARIO;
		case SHARES:
			return RESOURCE_NAV_LABEL_INVESTMENTS;
		case SUPERANNUATION:
			return RESOURCE_NAV_LABEL_INVESTMENTS;
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
	}

	public List<String> getListOfResourceTypesToCreate() {
		boolean propertyExisting = false;
		boolean propertyNew = false;
		boolean mortgageOffset = false;
		for (Resource resource : scenario.getResources()) {
			if (resource instanceof ResourcePropertyExisting) {
				propertyExisting = true;
				mortgageOffset = true;
			} else if (resource instanceof ResourcePropertyNew) {
				propertyNew = true;
				mortgageOffset = true;
			} 
		}
		List<String> resourceTypeNames = new ArrayList<String>();
		resourceTypeNames.add(ResourceType.CREDIT_CARD.getValue());
		resourceTypeNames.add(ResourceType.CURRENT_ACCOUNT.getValue());
		if (propertyExisting || propertyNew) resourceTypeNames.add(ResourceType.MORTGAGE.getValue());
		if (mortgageOffset) resourceTypeNames.add(ResourceType.MORTGAGE_OFFSET_ACCOUNT.getValue());
		resourceTypeNames.add(ResourceType.PERSON_ADULT.getValue());
		resourceTypeNames.add(ResourceType.PERSON_CHILD.getValue());
		resourceTypeNames.add(ResourceType.PROPERTY_EXISTING.getValue());
		resourceTypeNames.add(ResourceType.PROPERTY_NEW.getValue());
		resourceTypeNames.add(ResourceType.SAVINGS_ACCOUNT.getValue());
		resourceTypeNames.add(ResourceType.SHARES.getValue());
		resourceTypeNames.add(ResourceType.SUPERANNUATION.getValue());
		return resourceTypeNames;
	}
	
	// -------------------
	// Getters & setters.
	// -------------------

	public String getScenarioTabLabel() {
		return RESOURCE_NAV_LABEL_SCENARIO;
	}
	
	public Long getScenarioResourceId() {
		return scenario.getResourceScenario().getId();
	}
	
	public List<ResourceDropdownTabDto> getResourceTabs() {
		return new ArrayList<ResourceDropdownTabDto>(resourceTabMap.values());
	}
	
	public String getActiveResourceNavLabel() {
		return getResourceNavLabel(activeResourceType);
	}
}
