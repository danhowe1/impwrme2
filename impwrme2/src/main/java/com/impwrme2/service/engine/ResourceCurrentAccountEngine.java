package com.impwrme2.service.engine;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.impwrme2.model.cashflow.CashflowCategory;
import com.impwrme2.model.journalEntry.JournalEntry;
import com.impwrme2.model.resource.ResourceCurrentAccount;
import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValue;
import com.impwrme2.service.journalEntry.BalanceTracker;
import com.impwrme2.service.journalEntry.JournalEntryFactory;

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

	@Override
	public List<JournalEntry> generateJournalEntries(YearMonth yearMonth, BalanceTracker balanceTracker) {

		List<JournalEntry> journalEntries = new ArrayList<JournalEntry>();

		// Monthly interest.
		BigDecimal interestRate = interestRate(yearMonth);
		BigDecimal monthlyInterestRate = calculateMonthlyGrowthRateFromAnnualGrowthRate(interestRate);
		BigDecimal monthlyInterestAmount = BigDecimal.valueOf(balanceTracker.getResourceLiquidBalancePreviousMonth(getResource())).multiply(monthlyInterestRate);
		if (!monthlyInterestAmount.equals(BigDecimal.ZERO)) {
			journalEntries.add(JournalEntryFactory.create(getResource(), yearMonth, integerOf(monthlyInterestAmount), CashflowCategory.INCOME_INTEREST));
		}
		
		// Standard cash-flows.
		journalEntries.addAll(generateJournalEntriesFromCashflows(yearMonth, resource.getCashflows()));

		return journalEntries;
	}

	private BigDecimal interestRate(final YearMonth yearMonth) {
		Optional<ResourceParamDateValue<?>> interestRateOpt = (Optional<ResourceParamDateValue<?>>) getResource().getResourceParamDateValue(ResourceParamNameEnum.CURRENT_ACCOUNT_INTEREST_RATE, yearMonth);
		if (interestRateOpt.isEmpty()) {
			return BigDecimal.ZERO;
		}
		return (BigDecimal) interestRateOpt.get().getValue();
	}
}
