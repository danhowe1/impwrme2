package com.impwrme2.model.resourceParam;

import java.math.BigDecimal;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(ResourceParamType.Values.BIG_DECIMAL)
public class ResourceParamBigDecimal extends ResourceParam<BigDecimal> {

	private static final long serialVersionUID = -8194017381033422898L;

	protected ResourceParamBigDecimal() {
		super();
	}
	
	public ResourceParamBigDecimal(String name) {
		super(name);
	}

	@Override
	public ResourceParamType getResourceParamType() {
		return ResourceParamType.BIG_DECIMAL;
	}
}
