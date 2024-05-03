package com.impwrme2.service.engine;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.impwrme2.model.cashflow.Cashflow;
import com.impwrme2.model.cashflow.CashflowCategory;
import com.impwrme2.model.journalEntry.JournalEntry;
import com.impwrme2.model.resource.ResourceSuperannuation;
import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValue;
import com.impwrme2.service.journalEntry.BalanceTracker;
import com.impwrme2.service.journalEntry.JournalEntryFactory;

public class ResourceSuperannuationEngine extends ResourceEngine {

	private final YearMonth preservationDate;
	private boolean payOutDateReached = false;
	
	public ResourceSuperannuationEngine(final ResourceSuperannuation resource, final BalanceTracker balanceTracker) {
		super(resource, balanceTracker);
		Integer preservationAge = (Integer)  resource.getResourceParamDateValue(ResourceParamNameEnum.SUPER_PRESERVATION_AGE, resource.getStartYearMonth()).get().getValue();
		YearMonth birthYearMonth = (YearMonth)  resource.getParent().getResourceParamDateValue(ResourceParamNameEnum.PERSON_BIRTH_YEAR_MONTH, resource.getStartYearMonth()).get().getValue();
		preservationDate = birthYearMonth.plusYears(preservationAge);
	}

	@Override
	public Integer getBalanceLiquidLegalMaxIfNotSpecified(BalanceTracker balanceTracker) {
		return Integer.valueOf(0);
	}

	@Override
	public Integer getBalanceLiquidLegalMinIfNotSpecified() {
		return Integer.valueOf(0);
	}

	@Override
	public Integer getBalanceLiquidPreferredMaxIfNotSpecified(BalanceTracker balanceTracker) {
		return Integer.valueOf(0);
	}

	@Override
	public Integer getBalanceLiquidPreferredMinIfNotSpecified() {
		return Integer.valueOf(0);
	}

	@Override
	public List<JournalEntry> generateJournalEntries(YearMonth yearMonth, BalanceTracker balanceTracker) {

		List<JournalEntry> journalEntries = new ArrayList<JournalEntry>();

		if (yearMonth.isBefore(getResource().getStartYearMonth())) return journalEntries;
		
		if (payOutDateReached) return journalEntries;

		if (isPayOutMonth(yearMonth)) {
			Integer closingBalance = balanceTracker.getResourceFixedAmount(resource);
			journalEntries.add(JournalEntryFactory.create(getResource(), yearMonth, closingBalance, CashflowCategory.INCOME_ASSET_SALE));
			journalEntries.add(JournalEntryFactory.create(getResource(), yearMonth, -closingBalance, CashflowCategory.DEPRECIATION_SALE));
			return journalEntries;
		}

		// Monthly appreciation from share market growth.
		journalEntries.add(createJournalEntryForMonthlyAppreciationShareMarketGrowth(yearMonth, balanceTracker));

		// Monthly depreciation from annual management fee.
		journalEntries.add(createJournalEntryForMonthlyDepreciationAnnualManagementFee(yearMonth, balanceTracker));

		// Standard cash-flows.
		List<JournalEntry> standardJournalEntries = generateJournalEntriesFromCashflows(yearMonth, resource.getCashflows());
		journalEntries.addAll(standardJournalEntries);

		for (JournalEntry journalEntry : standardJournalEntries) {
			if (journalEntry.getCategory().equals(CashflowCategory.APPRECIATION_SUPER_CONTRIBUTION_CONCESSIONAL_PERSONAL)) {
				journalEntries.add(JournalEntryFactory.create(getResource(), yearMonth, -journalEntry.getAmount(), CashflowCategory.EXPENSE_SUPER_CONTRIBUTION_CONCESSIONAL_PERSONAL));				
			} else if (journalEntry.getCategory().equals(CashflowCategory.APPRECIATION_SUPER_CONTRIBUTION_NON_CONCESSIONAL_PERSONAL)) {
				journalEntries.add(JournalEntryFactory.create(getResource(), yearMonth, -journalEntry.getAmount(), CashflowCategory.EXPENSE_SUPER_CONTRIBUTION_NON_CONCESSIONAL_PERSONAL));				
			}
		}
		
		return journalEntries;
	}

	private boolean isPayOutMonth(final YearMonth yearMonth) {
		if (preservationDate.equals(yearMonth)) {
			payOutDateReached = true;
			return true;
		}
		return false;
	}

	private JournalEntry createJournalEntryForMonthlyAppreciationShareMarketGrowth(final YearMonth yearMonth, BalanceTracker balanceTracker) {
		BigDecimal growthRate = shareMarketGrowthRate(yearMonth);
		BigDecimal monthlyGrowthRate = calculateMonthlyGrowthRateFromAnnualGrowthRate(growthRate);
		BigDecimal amount = BigDecimal.valueOf(balanceTracker.getResourceFixedAmountPreviousMonth(getResource())).multiply(monthlyGrowthRate);
		return JournalEntryFactory.create(getResource(), yearMonth, integerOf(amount), CashflowCategory.APPRECIATION_GROWTH_FIXED);
	}

	private BigDecimal shareMarketGrowthRate(final YearMonth yearMonth) {
		Optional<ResourceParamDateValue<?>> growthRateOpt = getResource().getResourceParamDateValue(ResourceParamNameEnum.SUPER_GROWTH_RATE, yearMonth);
		return (BigDecimal) growthRateOpt.get().getValue();
	}

	private JournalEntry createJournalEntryForMonthlyDepreciationAnnualManagementFee(final YearMonth yearMonth, BalanceTracker balanceTracker) {
		BigDecimal growthRate = depreciationAnnualManagementFeeRate(yearMonth);
		BigDecimal monthlyGrowthRate = calculateMonthlyGrowthRateFromAnnualGrowthRate(growthRate);
		BigDecimal amount = BigDecimal.valueOf(balanceTracker.getResourceFixedAmountPreviousMonth(getResource())).multiply(monthlyGrowthRate);
		return JournalEntryFactory.create(getResource(), yearMonth, -integerOf(amount), CashflowCategory.DEPRECIATION_MANAGEMENT_FEE);
	}

	private BigDecimal depreciationAnnualManagementFeeRate(final YearMonth yearMonth) {
		Optional<ResourceParamDateValue<?>> growthRateOpt = getResource().getResourceParamDateValue(ResourceParamNameEnum.SUPER_MANAGEMENT_FEE_ANNUAL_PERCENTAGE, yearMonth);
		return (BigDecimal) growthRateOpt.get().getValue();
	}
}