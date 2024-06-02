package com.impwrme2.service.engine;

import java.time.YearMonth;

import com.impwrme2.model.resource.Resource;
import com.impwrme2.model.resource.ResourceMortgageOffset;
import com.impwrme2.service.journalEntry.BalanceTracker;

public class ResourceMortgageOffsetEngine extends ResourceEngine {

	public ResourceMortgageOffsetEngine(final ResourceMortgageOffset resource, final BalanceTracker balanceTracker) {
		super(resource, balanceTracker);
	}
	
	@Override
	public Integer getBalanceLiquidLegalMaxIfNotSpecified(BalanceTracker balanceTracker) {
		return Integer.MAX_VALUE;
	}

	@Override
	public Integer getBalanceLiquidLegalMinIfNotSpecified() {
		return Integer.valueOf(0);
	}

	@Override
	public Integer getBalanceLiquidPreferredMaxIfNotSpecified(final YearMonth yearMonth, final BalanceTracker balanceTracker) {
		return calculatePreferredMax(balanceTracker);
	}

//	@Override
//	public Integer getBalanceLiquidPreferredMinIfNotSpecified() {
//		return Integer.valueOf(0);
//	}

	private Integer calculatePreferredMax(final BalanceTracker balanceTracker) {
		// Set to mortgage balance.
		Integer preferredMaxBalance = -balanceTracker.getResourceFixedAmount(getResource().getParent());
		// Take off additional repayments.
		preferredMaxBalance = preferredMaxBalance - balanceTracker.getResourceLiquidDepositAmount(getResource().getParent());
		for (Resource resource : getResource().getParent().getChildren()) {
			if (resource instanceof ResourceMortgageOffset && resource.getId().intValue() != getResource().getId().intValue()) {
				// Remove all balances from other offset accounts.
				preferredMaxBalance = preferredMaxBalance - balanceTracker.getResourceLiquidBalance(resource);
			}
		}
		return Math.max(0, preferredMaxBalance);
	}
}
