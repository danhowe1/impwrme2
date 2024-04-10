package com.impwrme2.service.engine;

import com.impwrme2.model.resource.ResourceCurrentAccount;

public class ResourceCurrentAccountEngine extends ResourceEngine {

	public ResourceCurrentAccountEngine(ResourceCurrentAccount resource) {
		super(resource);
	}

	@Override
	public Integer getBalanceLiquidLegalMaxIfNotSpecified() {
		return Integer.MAX_VALUE;
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
