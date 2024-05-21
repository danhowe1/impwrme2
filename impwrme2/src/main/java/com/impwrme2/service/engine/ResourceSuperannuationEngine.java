package com.impwrme2.service.engine;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.impwrme2.model.cashflow.CashflowCategory;
import com.impwrme2.model.journalEntry.JournalEntry;
import com.impwrme2.model.milestone.Milestone;
import com.impwrme2.model.milestone.MilestoneType;
import com.impwrme2.model.resource.ResourceSuperannuation;
import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValue;
import com.impwrme2.service.journalEntry.BalanceTracker;
import com.impwrme2.service.journalEntry.JournalEntryFactory;

public class ResourceSuperannuationEngine extends ResourceEngine {

	private final Integer preservationAge;
	private final YearMonth preservationDate;
	private boolean payOutDateReached = false;
	
	public ResourceSuperannuationEngine(final ResourceSuperannuation resource, final BalanceTracker balanceTracker) {
		super(resource, balanceTracker);
		preservationAge = (Integer)  resource.getResourceParamDateValue(ResourceParamNameEnum.SUPER_PRESERVATION_AGE, resource.getStartYearMonth()).get().getValue();
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
	public List<JournalEntry> generateJournalEntries(YearMonth yearMonth, BalanceTracker balanceTracker) {

		List<JournalEntry> journalEntries = new ArrayList<JournalEntry>();
		if (yearMonth.isBefore(getResource().getStartYearMonth())) return journalEntries;
		if (yearMonth.equals(preservationDate)) {
			addMilestone(yearMonth, MilestoneType.SUPER_PRESERVATION_AGE, getResource().getName(), String.valueOf(preservationAge), Milestone.NUMBER_FORMAT.format(balanceTracker.getResourceAssetValue(getResource())));
		}

		if (payOutDateReached) return journalEntries;
		
		// Monthly appreciation from share market growth.
		BigDecimal appreciationGrowth = calculateMonthlyAppreciationShareMarketGrowth(yearMonth, balanceTracker);
		journalEntries.add(JournalEntryFactory.create(getResource(), yearMonth, integerOf(appreciationGrowth), CashflowCategory.APPRECIATION_GROWTH_FIXED));

		// Monthly depreciation from annual management fee.
		BigDecimal managementFee = calculateMonthlyDepreciationAnnualManagementFee(yearMonth, balanceTracker);
		journalEntries.add(JournalEntryFactory.create(getResource(), yearMonth, -integerOf(managementFee), CashflowCategory.DEPRECIATION_MANAGEMENT_FEE));

		if (isPayOutMonth(yearMonth)) {
			Integer closingBalance = balanceTracker.getResourceFixedAmount(resource);
			// We include this month's appreciation because if we didn't and transferred this balance into Shares, the Shares
			// balance wouldn't update until next month. In this case neither this Super or the Shares would have counted the
			// monthly growth.
			closingBalance = closingBalance + integerOf(appreciationGrowth) - integerOf(managementFee);
			journalEntries.add(JournalEntryFactory.create(getResource(), yearMonth, closingBalance, CashflowCategory.INCOME_ASSET_SALE));
			journalEntries.add(JournalEntryFactory.create(getResource(), yearMonth, -closingBalance, CashflowCategory.DEPRECIATION_SALE));
			return journalEntries;
		}

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

	private BigDecimal calculateMonthlyAppreciationShareMarketGrowth(final YearMonth yearMonth, BalanceTracker balanceTracker) {
		BigDecimal growthRate = shareMarketGrowthRate(yearMonth);
		BigDecimal monthlyGrowthRate = calculateMonthlyGrowthRateFromAnnualGrowthRate(growthRate);
		BigDecimal amount = BigDecimal.valueOf(balanceTracker.getResourceFixedAmountPreviousMonth(getResource())).multiply(monthlyGrowthRate);
		return amount;
	}
	
//	private JournalEntry createJournalEntryForMonthlyAppreciationShareMarketGrowth(final YearMonth yearMonth, BalanceTracker balanceTracker) {
//		BigDecimal growthRate = shareMarketGrowthRate(yearMonth);
//		BigDecimal monthlyGrowthRate = calculateMonthlyGrowthRateFromAnnualGrowthRate(growthRate);
//		BigDecimal amount = BigDecimal.valueOf(balanceTracker.getResourceFixedAmountPreviousMonth(getResource())).multiply(monthlyGrowthRate);
//		return JournalEntryFactory.create(getResource(), yearMonth, integerOf(amount), CashflowCategory.APPRECIATION_GROWTH_FIXED);
//	}

	private BigDecimal shareMarketGrowthRate(final YearMonth yearMonth) {
		Optional<ResourceParamDateValue<?>> growthRateOpt = getResource().getResourceParamDateValue(ResourceParamNameEnum.SUPER_GROWTH_RATE, yearMonth);
		return (BigDecimal) growthRateOpt.get().getValue();
	}

	private BigDecimal calculateMonthlyDepreciationAnnualManagementFee(final YearMonth yearMonth, BalanceTracker balanceTracker) {
		BigDecimal growthRate = depreciationAnnualManagementFeeRate(yearMonth);
		BigDecimal monthlyGrowthRate = calculateMonthlyGrowthRateFromAnnualGrowthRate(growthRate);
		BigDecimal amount = BigDecimal.valueOf(balanceTracker.getResourceFixedAmountPreviousMonth(getResource())).multiply(monthlyGrowthRate);
		return amount;
	}
	
//	private JournalEntry createJournalEntryForMonthlyDepreciationAnnualManagementFee(final YearMonth yearMonth, BalanceTracker balanceTracker) {
//		BigDecimal growthRate = depreciationAnnualManagementFeeRate(yearMonth);
//		BigDecimal monthlyGrowthRate = calculateMonthlyGrowthRateFromAnnualGrowthRate(growthRate);
//		BigDecimal amount = BigDecimal.valueOf(balanceTracker.getResourceFixedAmountPreviousMonth(getResource())).multiply(monthlyGrowthRate);
//		return JournalEntryFactory.create(getResource(), yearMonth, -integerOf(amount), CashflowCategory.DEPRECIATION_MANAGEMENT_FEE);
//	}

	private BigDecimal depreciationAnnualManagementFeeRate(final YearMonth yearMonth) {
		Optional<ResourceParamDateValue<?>> growthRateOpt = getResource().getResourceParamDateValue(ResourceParamNameEnum.SUPER_MANAGEMENT_FEE_ANNUAL_PERCENTAGE, yearMonth);
		return (BigDecimal) growthRateOpt.get().getValue();
	}
}