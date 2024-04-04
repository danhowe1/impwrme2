package com.impwrme2.model.resourceParam;

import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValue;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(ResourceParamType.Values.INTEGER_NEGATIVE)
public class ResourceParamIntegerNegative extends ResourceParam<Integer> {

	private static final long serialVersionUID = -5671677115458203650L;

	protected ResourceParamIntegerNegative() {
		super();
	}
	
	public ResourceParamIntegerNegative(final ResourceParamNameEnum paramName) {
		super(paramName);
	}

	@Override
	public ResourceParamType getResourceParamType() {
		return ResourceParamType.INTEGER_NEGATIVE;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addResourceParamDateValueGeneric(ResourceParamDateValue<?> resourceParamDateValue) {
		this.addResourceParamDateValue((ResourceParamDateValue<Integer>) resourceParamDateValue);
	}
}
