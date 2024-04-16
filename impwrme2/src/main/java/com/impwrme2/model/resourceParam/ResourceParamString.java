package com.impwrme2.model.resourceParam;

import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValue;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(ResourceParamType.Values.STRING)
public class ResourceParamString extends ResourceParam<String> {

	private static final long serialVersionUID = -5492358794188721166L;

	protected ResourceParamString() {
		super();
	}
	
	public ResourceParamString(final ResourceParamNameEnum paramName) {
		super(paramName);
	}

	@Override
	public ResourceParamType getResourceParamType() {
		return ResourceParamType.STRING;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addResourceParamDateValueGeneric(ResourceParamDateValue<?> resourceParamDateValue) {
		this.addResourceParamDateValue((ResourceParamDateValue<String>) resourceParamDateValue);
	}
}
