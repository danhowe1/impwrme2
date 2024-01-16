package com.impwrme2.model.resource;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(ResourceType.Values.PERSON)
public class ResourcePerson extends Resource {

	private static final long serialVersionUID = -3109504020949719141L;

	/**
	 * Protected constructor required for Hibernate only.
	 */
	protected ResourcePerson() {
		super();
	}
	
	public ResourcePerson(final String name, final Resource parent) {
		super(name, parent);
	}

	@Override
	public ResourceType getResourceType() {
		return ResourceType.PERSON;
	}
}
