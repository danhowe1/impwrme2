package com.impwrme2.model.resourceParam;

import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValue;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(ResourceParamType.Values.INTEGER)
public class ResourceParamInteger extends ResourceParam<Integer> {

	private static final long serialVersionUID = 1946945926952691494L;

	protected ResourceParamInteger() {
		super();
	}
	
	public ResourceParamInteger(final ResourceParamNameEnum paramName) {
		super(paramName);
	}

	@Override
	public ResourceParamType getResourceParamType() {
		return ResourceParamType.INTEGER;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addResourceParamDateValueGeneric(ResourceParamDateValue<?> resourceParamDateValue) {
		this.addResourceParamDateValue((ResourceParamDateValue<Integer>) resourceParamDateValue);
	}
}
