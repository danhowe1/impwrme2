package com.impwrme2.model.resource;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(ResourceType.Values.UNALLOCATED)
public class ResourceUnallocated extends Resource {

	private static final long serialVersionUID = 4110159341043410508L;

	/**
	 * Protected constructor required for Hibernate only.
	 */
	protected ResourceUnallocated() {
		super();
	}
	
	public ResourceUnallocated(final String name) {
		super(name);
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.UNALLOCATED;
	}
}
