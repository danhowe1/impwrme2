package com.impwrme2.model.resource;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(ResourceType.Values.PROPERTY_NEW)
public class ResourcePropertyNew extends ResourcePropertyExisting {

	private static final long serialVersionUID = 2191667699136239436L;

	/**
	 * Protected constructor required for Hibernate only.
	 */
	protected ResourcePropertyNew() {
		super();
	}
	
	public ResourcePropertyNew(final String name) {
		super(name);
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.PROPERTY_NEW;
	}
}
