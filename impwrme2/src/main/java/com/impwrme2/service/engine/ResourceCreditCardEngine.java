package com.impwrme2.service.engine;

import java.time.YearMonth;

import com.impwrme2.model.resource.ResourceCreditCard;

public class ResourceCreditCardEngine extends ResourceEngine {

	public ResourceCreditCardEngine(ResourceCreditCard resource) {
		super(resource);
	}

	@Override
	public Integer getBalanceLiquidLegalMax(YearMonth yearMonth) {
		return Integer.valueOf(0);
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
		return Integer.valueOf(0);
	}

	@Override
	public Integer getBalanceLiquidPreferredMin(YearMonth yearMonth) {
		return Integer.valueOf(0);
	}
}
