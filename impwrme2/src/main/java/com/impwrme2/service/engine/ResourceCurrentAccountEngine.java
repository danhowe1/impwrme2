package com.impwrme2.service.engine;

import java.time.YearMonth;

import com.impwrme2.model.resource.ResourceCurrentAccount;

public class ResourceCurrentAccountEngine extends ResourceEngine {

	public ResourceCurrentAccountEngine(ResourceCurrentAccount resource) {
		super(resource);
	}

	@Override
	public Integer getBalanceLiquidLegalMax(YearMonth yearMonth) {
		Integer val = super.getBalanceLiquidLegalMax(yearMonth);
		if (null == val) {
			return Integer.MAX_VALUE;
		}
		return val;
	}

	@Override
	public Integer getBalanceLiquidLegalMin(YearMonth yearMonth) {
		Integer val = super.getBalanceLiquidLegalMin(yearMonth);
		if (null == val) {
			return Integer.valueOf(0);
		}
		return val;
	}

	@Override
	public Integer getBalanceLiquidPreferredMax(YearMonth yearMonth) {
		Integer val = super.getBalanceLiquidPreferredMax(yearMonth);
		if (null == val) {
			return Integer.MAX_VALUE;
		}
		return val;
	}

	@Override
	public Integer getBalanceLiquidPreferredMin(YearMonth yearMonth) {
		Integer val = super.getBalanceLiquidPreferredMin(yearMonth);
		if (null == val) {
			return Integer.valueOf(0);
		}
		return val;
	}
}
