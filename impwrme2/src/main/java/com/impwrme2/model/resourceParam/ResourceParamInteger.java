package com.impwrme2.model.resourceParam;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(ResourceParamType.Values.INTEGER)
public class ResourceParamInteger extends ResourceParam<Integer> {

	private static final long serialVersionUID = 1946945926952691494L;

	protected ResourceParamInteger() {
		super();
	}
	
	public ResourceParamInteger(String name) {
		super(name);
	}

	@Override
	public ResourceParamType getResourceParamType() {
		return ResourceParamType.INTEGER;
	}
}