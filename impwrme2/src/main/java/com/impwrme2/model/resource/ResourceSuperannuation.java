package com.impwrme2.model.resource;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(ResourceType.Values.SUPERANNUATION)
public class ResourceSuperannuation extends Resource {

	private static final long serialVersionUID = 5028205240793185084L;

	/**
	 * Protected constructor required for Hibernate only.
	 */
	protected ResourceSuperannuation() {
		super();
	}
	
	public ResourceSuperannuation(final String name) {
		super(name);
	}
	@Override
	public ResourceType getResourceType() {
		return ResourceType.SUPERANNUATION;
	}
}
