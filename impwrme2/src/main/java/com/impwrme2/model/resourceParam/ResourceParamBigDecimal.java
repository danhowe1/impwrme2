package com.impwrme2.model.resourceParam;

import java.math.BigDecimal;

import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValue;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(ResourceParamType.Values.BIG_DECIMAL)
public class ResourceParamBigDecimal extends ResourceParam<BigDecimal> {

	private static final long serialVersionUID = -8194017381033422898L;

	protected ResourceParamBigDecimal() {
		super();
	}
	
	public ResourceParamBigDecimal(final ResourceParamNameEnum paramName) {
		super(paramName);
	}

	@Override
	public ResourceParamType getResourceParamType() {
		return ResourceParamType.BIG_DECIMAL;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addResourceParamDateValueGeneric(ResourceParamDateValue<?> resourceParamDateValue) {
		this.addResourceParamDateValue((ResourceParamDateValue<BigDecimal>) resourceParamDateValue);
	}
}
