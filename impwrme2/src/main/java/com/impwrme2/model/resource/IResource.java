package com.impwrme2.model.resource;


public interface IResource {

	public ResourceType getResourceType();
	public ResourceScenario getResourceScenario();
	public String getPrioritisationWithinResourceType();
}
