package com.impwrme2.model.resourceParam;

import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValue;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(ResourceParamType.Values.INTEGER_POSITIVE)
public class ResourceParamIntegerPositive extends ResourceParam<Integer> {

	private static final long serialVersionUID = -9044214743327896634L;

	protected ResourceParamIntegerPositive() {
		super();
	}
	
	public ResourceParamIntegerPositive(final ResourceParamNameEnum paramName) {
		super(paramName);
	}

	@Override
	public ResourceParamType getResourceParamType() {
		return ResourceParamType.INTEGER_POSITIVE;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addResourceParamDateValueGeneric(ResourceParamDateValue<?> resourceParamDateValue) {
		this.addResourceParamDateValue((ResourceParamDateValue<Integer>) resourceParamDateValue);
	}
}
