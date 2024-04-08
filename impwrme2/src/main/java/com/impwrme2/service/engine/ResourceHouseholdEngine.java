package com.impwrme2.service.engine;

import java.time.YearMonth;

import com.impwrme2.model.resource.ResourceHousehold;

public class ResourceHouseholdEngine extends ResourceEngine {

	public ResourceHouseholdEngine(ResourceHousehold resource) {
		super(resource);
	}

	@Override
	public Integer getBalanceLiquidLegalMax(YearMonth yearMonth) {
		return Integer.valueOf(0);
	}

	@Override
	public Integer getBalanceLiquidLegalMin(YearMonth yearMonth) {
		return Integer.valueOf(0);
	}

	@Override
	public Integer getBalanceLiquidPreferredMax(YearMonth yearMonth) {
		return Integer.valueOf(0);
	}

	@Override
	public Integer getBalanceLiquidPreferredMin(YearMonth yearMonth) {
		return Integer.valueOf(0);
	}
}
