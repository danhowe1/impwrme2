package com.impwrme2.model.resource;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(ResourceType.Values.HOUSEHOLD)
public class ResourceHousehold extends Resource {

	private static final long serialVersionUID = -8656133449708260413L;

	/**
	 * Protected constructor required for Hibernate only.
	 */
	protected ResourceHousehold() {
		super();
	}
	
	public ResourceHousehold(final String name) {
		super(name);
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.HOUSEHOLD;
	}
}
