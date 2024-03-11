package com.impwrme2.service.engine;

import java.math.BigDecimal;
import java.time.YearMonth;

import com.impwrme2.model.resource.ResourceCurrentAccount;

public class ResourceCurrentAccountEngine extends ResourceEngine {

	public ResourceCurrentAccountEngine(ResourceCurrentAccount resource) {
		super(resource);
	}

	@Override
	public BigDecimal getBalanceLiquidLegalMax(YearMonth yearMonth) {
		return BigDecimal.valueOf(Integer.MAX_VALUE);
	}

	@Override
	public BigDecimal getBalanceLiquidLegalMin(YearMonth yearMonth) {
		return BigDecimal.ZERO;
	}
}
