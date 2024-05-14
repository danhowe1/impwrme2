package com.impwrme2.service.engine;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.impwrme2.model.cashflow.CashflowCategory;
import com.impwrme2.model.journalEntry.JournalEntry;
import com.impwrme2.model.resource.ResourceSavingsAccount;
import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValue;
import com.impwrme2.service.journalEntry.BalanceTracker;
import com.impwrme2.service.journalEntry.JournalEntryFactory;

public class ResourceSavingsAccountEngine extends ResourceEngine {

	public ResourceSavingsAccountEngine(ResourceSavingsAccount resource, final BalanceTracker balanceTracker) {
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
	public List<JournalEntry> generateJournalEntries(YearMonth yearMonth, BalanceTracker balanceTracker) {

		List<JournalEntry> journalEntries = new ArrayList<JournalEntry>();
		if (yearMonth.isBefore(getResource().getStartYearMonth())) return journalEntries;

		// Monthly interest.
		BigDecimal interestRate = interestRate(yearMonth);
		BigDecimal monthlyInterestRate = calculateMonthlyGrowthRateFromAnnualGrowthRate(interestRate);
		BigDecimal monthlyInterestAmount = BigDecimal.valueOf(balanceTracker.getResourceLiquidBalancePreviousMonth(getResource())).multiply(monthlyInterestRate);
		if (monthlyInterestAmount.intValue() > 0) {
			journalEntries.add(JournalEntryFactory.create(getResource(), yearMonth, integerOf(monthlyInterestAmount), CashflowCategory.APPRECIATION_INTEREST_LIQUID));
		}
		
		// Standard cash-flows.
		journalEntries.addAll(generateJournalEntriesFromCashflows(yearMonth, resource.getCashflows()));

		return journalEntries;
	}

	private BigDecimal interestRate(final YearMonth yearMonth) {
		Optional<ResourceParamDateValue<?>> interestRateOpt = (Optional<ResourceParamDateValue<?>>) getResource().getResourceParamDateValue(ResourceParamNameEnum.SAVINGS_ACCOUNT_INTEREST_RATE, yearMonth);
		if (interestRateOpt.isEmpty()) {
			return BigDecimal.ZERO;
		}
		return (BigDecimal) interestRateOpt.get().getValue();
	}
}
