package com.impwrme2.model.resource;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(ResourceType.Values.MORTGAGE_EXISTING)
public class ResourceMortgageExisting extends Resource {

	private static final long serialVersionUID = -2859351843058300669L;

	@Override
	public ResourceType getResourceType() {
		return ResourceType.MORTGAGE_EXISTING;
	}

}
