package com.impwrme2.service.engine;

import com.impwrme2.model.resource.ResourceCurrentAccount;
import com.impwrme2.service.journalEntry.BalanceTracker;

public class ResourceCurrentAccountEngine extends ResourceEngine {

	public ResourceCurrentAccountEngine(ResourceCurrentAccount resource, final BalanceTracker balanceTracker) {
		super(resource, balanceTracker);
	}

	@Override
	public Integer getBalanceLiquidLegalMaxIfNotSpecified(final BalanceTracker balanceTracker) {
		return Integer.MAX_VALUE;
	}

	@Override
	public Integer getBalanceLiquidLegalMinIfNotSpecified() {
		return Integer.valueOf(0);
	}

	@Override
	public Integer getBalanceLiquidPreferredMaxIfNotSpecified(final BalanceTracker balanceTracker) {
		return getBalanceLiquidLegalMaxIfNotSpecified(balanceTracker);
	}

	@Override
	public Integer getBalanceLiquidPreferredMinIfNotSpecified() {
		return Integer.valueOf(0);
	}
}
