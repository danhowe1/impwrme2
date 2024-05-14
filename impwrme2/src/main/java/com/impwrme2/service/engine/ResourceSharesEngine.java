package com.impwrme2.service.engine;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.impwrme2.model.cashflow.CashflowCategory;
import com.impwrme2.model.journalEntry.JournalEntry;
import com.impwrme2.model.resource.ResourceShares;
import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValue;
import com.impwrme2.service.journalEntry.BalanceTracker;
import com.impwrme2.service.journalEntry.JournalEntryFactory;

public class ResourceSharesEngine extends ResourceEngine {

	public ResourceSharesEngine(final ResourceShares resource, final BalanceTracker balanceTracker) {
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

//	@Override
//	public Integer getBalanceLiquidPreferredMaxIfNotSpecified(BalanceTracker balanceTracker) {
//		return getBalanceLiquidLegalMaxIfNotSpecified(balanceTracker);
//	}
//
//	@Override
//	public Integer getBalanceLiquidPreferredMinIfNotSpecified() {
//		return Integer.valueOf(0);
//	}

	@Override
	public List<JournalEntry> generateJournalEntries(YearMonth yearMonth, BalanceTracker balanceTracker) {

		List<JournalEntry> journalEntries = new ArrayList<JournalEntry>();
		if (yearMonth.isBefore(getResource().getStartYearMonth())) return journalEntries;
		
		// Monthly appreciation from share market growth.
		journalEntries.add(createJournalEntryForMonthlyAppreciationShareMarketGrowth(yearMonth, balanceTracker));

		// Monthly dividend yield.
		journalEntries.addAll(createJournalEntriesForMonthlyDividendYield(yearMonth, balanceTracker));

		// Standard cash-flows.
		journalEntries.addAll(generateJournalEntriesFromCashflows(yearMonth, resource.getCashflows()));

		return journalEntries;
	}
	
	private JournalEntry createJournalEntryForMonthlyAppreciationShareMarketGrowth(final YearMonth yearMonth, BalanceTracker balanceTracker) {
		BigDecimal growthRate = shareMarketGrowthRate(yearMonth);
		BigDecimal monthlyGrowthRate = calculateMonthlyGrowthRateFromAnnualGrowthRate(growthRate);
		BigDecimal amount = BigDecimal.valueOf(balanceTracker.getResourceLiquidBalancePreviousMonth(getResource())).multiply(monthlyGrowthRate);
		return JournalEntryFactory.create(getResource(), yearMonth, integerOf(amount), CashflowCategory.APPRECIATION_GROWTH_LIQUID);
	}

	private BigDecimal shareMarketGrowthRate(final YearMonth yearMonth) {
		// Return the local growth rate if it exists.
		Optional<ResourceParamDateValue<?>> growthRateOpt = getResource().getResourceParamDateValue(ResourceParamNameEnum.SHARES_SHARE_MARKET_GROWTH_RATE, yearMonth);
		if (growthRateOpt.isPresent()) {
			return (BigDecimal) growthRateOpt.get().getValue();
		}
		// Return the scenario growth rate otherwise.
		growthRateOpt = getResource().getScenario().getResourceScenario().getResourceParamDateValue(ResourceParamNameEnum.SCENARIO_SHARE_MARKET_GROWTH_RATE, yearMonth);
		return (BigDecimal) growthRateOpt.get().getValue();
	}

	private List<JournalEntry> createJournalEntriesForMonthlyDividendYield(final YearMonth yearMonth, BalanceTracker balanceTracker) {
		List<JournalEntry> journalEntries = new ArrayList<JournalEntry>();
		BigDecimal dividendYield = dividendYield(yearMonth);
		BigDecimal monthlyDividendYield = calculateMonthlyGrowthRateFromAnnualGrowthRate(dividendYield);
		BigDecimal amount = BigDecimal.valueOf(balanceTracker.getResourceLiquidBalancePreviousMonth(getResource())).multiply(monthlyDividendYield);
		journalEntries.add(JournalEntryFactory.create(getResource(), yearMonth, integerOf(amount), CashflowCategory.INCOME_DIVIDENDS));
		if (dividendProcessing(yearMonth).equals(ResourceShares.SHARES_DIVIDEND_PROCESSING_REINVEST)) {
			// Re-investing. We want to ensure the deposited funds end up in the liquid deposit amount. As such, we create the income
			// but also a deposit so the income goes immediately into the shares account. NB: if there's not enough money in the pot
			// to generate the deposit this won't be created in the JournalEntryService.
			journalEntries.add(JournalEntryFactory.create(getResource(), yearMonth, integerOf(amount), CashflowCategory.DEPOSIT_DIVIDENDS));
		}
		
		return journalEntries;
	}

	private BigDecimal dividendYield(final YearMonth yearMonth) {
		Optional<ResourceParamDateValue<?>> purchaseCostsOpt = (Optional<ResourceParamDateValue<?>>) getResource().getResourceParamDateValue(ResourceParamNameEnum.SHARES_DIVIDEND_YIELD, yearMonth);
		return (BigDecimal) purchaseCostsOpt.get().getValue();
	}

	private String dividendProcessing(final YearMonth yearMonth) {
		Optional<ResourceParamDateValue<?>> dividendProcessingOpt = (Optional<ResourceParamDateValue<?>>) getResource().getResourceParamDateValue(ResourceParamNameEnum.SHARES_DIVIDEND_PROCESSING, yearMonth);
		return (String) dividendProcessingOpt.get().getValue();
	}
}
