package com.impwrme2.service.engine;

import java.time.YearMonth;

import com.impwrme2.model.resource.ResourceCurrentAccount;

public class ResourceCurrentAccountEngine extends ResourceEngine {

	public ResourceCurrentAccountEngine(ResourceCurrentAccount resource) {
		super(resource);
	}

	@Override
	public Integer getBalanceLiquidLegalMax(YearMonth yearMonth) {
		return Integer.MAX_VALUE;
	}
}
