package com.impwrme2.service.engine;

import com.impwrme2.model.resource.ResourceCreditCard;
import com.impwrme2.service.journalEntry.BalanceTracker;

public class ResourceCreditCardEngine extends ResourceEngine {

	public ResourceCreditCardEngine(final ResourceCreditCard resource, final BalanceTracker balanceTracker) {
		super(resource, balanceTracker);
	}

	@Override
	public Integer getBalanceLiquidLegalMaxIfNotSpecified(final BalanceTracker balanceTracker) {
		return Integer.valueOf(0);
	}

	@Override
	public Integer getBalanceLiquidLegalMinIfNotSpecified() {
		return Integer.valueOf(0);
	}

	@Override
	public Integer getBalanceLiquidPreferredMaxIfNotSpecified(final BalanceTracker balanceTracker) {
		return Integer.valueOf(0);
	}

	@Override
	public Integer getBalanceLiquidPreferredMinIfNotSpecified() {
		return Integer.valueOf(0);
	}
}
