package com.impwrme2.model.resource;

import com.impwrme2.model.scenario.Scenario;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(ResourceType.Values.FAMILY)
public class ResourceFamily extends Resource {

	public ResourceFamily(String name, Scenario scenario) {
		super(name, scenario);
	}

	private static final long serialVersionUID = -8656133449708260413L;

	@Override
	public ResourceType getResourceType() {
		return ResourceType.FAMILY;
	}
}
