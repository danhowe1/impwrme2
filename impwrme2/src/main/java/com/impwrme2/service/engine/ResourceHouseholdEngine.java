package com.impwrme2.service.engine;

import com.impwrme2.model.resource.ResourceHousehold;

public class ResourceHouseholdEngine extends ResourceEngine {

	public ResourceHouseholdEngine(ResourceHousehold resource) {
		super(resource);
	}

	@Override
	public Integer getBalanceLiquidLegalMaxIfNotSpecified() {
		return Integer.valueOf(0);
	}

	@Override
	public Integer getBalanceLiquidLegalMinIfNotSpecified() {
		return Integer.valueOf(0);
	}

	@Override
	public Integer getBalanceLiquidPreferredMaxIfNotSpecified() {
		return Integer.valueOf(0);
	}

	@Override
	public Integer getBalanceLiquidPreferredMinIfNotSpecified() {
		return Integer.valueOf(0);
	}
}
