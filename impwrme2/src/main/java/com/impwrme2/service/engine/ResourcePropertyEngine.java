package com.impwrme2.service.engine;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.impwrme2.model.cashflow.Cashflow;
import com.impwrme2.model.cashflow.CashflowCategory;
import com.impwrme2.model.journalEntry.JournalEntry;
import com.impwrme2.model.resource.ResourcePropertyExisting;
import com.impwrme2.model.resource.ResourcePropertyNew;
import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValue;
import com.impwrme2.service.journalEntry.BalanceTracker;
import com.impwrme2.service.journalEntry.JournalEntryFactory;

public class ResourcePropertyEngine extends ResourceEngine {

	public ResourcePropertyEngine(ResourcePropertyExisting resource) {
		super(resource);
	}

	public ResourcePropertyEngine(ResourcePropertyNew resource) {
		super(resource);
	}

	private boolean isSold = false;
	
	@Override
	public Integer getBalanceLiquidLegalMaxIfNotSpecified() {
		return Integer.valueOf(0);
	}

	@Override
	public Integer getBalanceLiquidLegalMinIfNotSpecified() {
		return Integer.valueOf(0);
	}

	@Override
	public Integer getBalanceLiquidPreferredMaxIfNotSpecified() {
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
		if (isSold) return journalEntries;

		@SuppressWarnings("unchecked")
		ResourceParamDateValue<String> status = (ResourceParamDateValue<String>) getResource().getResourceParamDateValue(ResourceParamNameEnum.PROPERTY_STATUS, yearMonth).get();

		// Purchase journal entries if required.
		journalEntries.addAll(createJournalEntriesForPurchase(status, balanceTracker, yearMonth));
		
		// Sale journal entries if required.
		journalEntries.addAll(createJournalEntriesForSale(status, balanceTracker, yearMonth));

		// If the property isn't rented we don't include the rental expenses and income.
		final List<Cashflow> cashflowsToProcess = new ArrayList<Cashflow>(getResource().getCashflows());
		if (!status.getValue().equals(ResourcePropertyExisting.PROPERTY_STATUS_RENTED)) {
			cashflowsToProcess.removeIf(value -> value.getCategory().equals(CashflowCategory.EXPENSE_RENTAL_PROPERTY) || value.getCategory().equals(CashflowCategory.INCOME_RENTAL_PROPERTY));			
		}

		// Monthly appreciation from property growth.
		journalEntries.add(createJournalEntryForMonthlyAppreciationPropertyGrowth(yearMonth, balanceTracker));
		
		// Standard cash-flows.
		journalEntries.addAll(generateJournalEntriesFromCashflows(yearMonth, cashflowsToProcess));
		
		return journalEntries;
	}
	private List<JournalEntry> createJournalEntriesForPurchase(ResourceParamDateValue<String> status, final BalanceTracker balanceTracker, final YearMonth yearMonth) {
		List<JournalEntry> journalEntries = new ArrayList<JournalEntry>();
		if (getResource() instanceof ResourcePropertyNew && getResource().getStartYearMonth().equals(yearMonth)) {
			journalEntries.add(JournalEntryFactory.create(getResource(), yearMonth, purchaseDeposit(), CashflowCategory.EXPENSE_ASSET_PURCHASE_DEPOSIT));
			journalEntries.add(JournalEntryFactory.create(getResource(), yearMonth, purchaseCosts(), CashflowCategory.EXPENSE_ASSET_PURCHASE_ADDITIONAL_COSTS));
		}
		return journalEntries;
	}
	
	private Integer purchaseDeposit() {
		Optional<ResourceParamDateValue<?>> purchaseDepositOpt = (Optional<ResourceParamDateValue<?>>) getResource().getResourceParamDateValue(ResourceParamNameEnum.PROPERTY_ASSET_PURCHASE_DEPOSIT, getResource().getStartYearMonth());
		return (Integer) purchaseDepositOpt.get().getValue();		
	}
	
	private Integer purchaseCosts() {
		Optional<ResourceParamDateValue<?>> purchaseCostsOpt = (Optional<ResourceParamDateValue<?>>) getResource().getResourceParamDateValue(ResourceParamNameEnum.PROPERTY_ASSET_PURCHASE_ADDITIONAL_COSTS, getResource().getStartYearMonth());
		return (Integer) purchaseCostsOpt.get().getValue();
	}
	
	private List<JournalEntry> createJournalEntriesForSale(ResourceParamDateValue<String> status, final BalanceTracker balanceTracker, final YearMonth yearMonth) {
		List<JournalEntry> journalEntries = new ArrayList<JournalEntry>();

		if (isSaleMonth(status, yearMonth)) {
			Integer saleAmount = balanceTracker.getResourceFixedAmount(getResource());
			journalEntries.add(JournalEntryFactory.create(getResource(), yearMonth, saleAmount, CashflowCategory.INCOME_ASSET_SALE));
			journalEntries.add(JournalEntryFactory.create(getResource(), yearMonth, -saleAmount, CashflowCategory.DEPRECIATION_SALE));
			journalEntries.add(createJournalEntryForExpenseSaleCommission(yearMonth, balanceTracker));
			journalEntries.add(createJournalEntryForExpenseSaleFixed(yearMonth, balanceTracker));
		}

		return journalEntries;
	}
	
	private boolean isSaleMonth(final ResourceParamDateValue<String> status, final YearMonth yearMonth) {
		if (status.getValue().equals(ResourcePropertyExisting.PROPERTY_STATUS_SOLD) &&
			status.getYearMonth().equals(yearMonth)) {
			isSold = true;
			return true;
		}
		return false;
	}

	private JournalEntry createJournalEntryForMonthlyAppreciationPropertyGrowth(final YearMonth yearMonth, BalanceTracker balanceTracker) {
		BigDecimal growthRate = propertyGrowthRate(yearMonth);
		BigDecimal monthlyGrowthRate = calculateMonthlyGrowthRateFromAnnualGrowthRate(growthRate);
		BigDecimal amount = BigDecimal.valueOf(balanceTracker.getResourceFixedAmount(getResource())).multiply(monthlyGrowthRate);
		return JournalEntryFactory.create(getResource(), yearMonth, integerOf(amount), CashflowCategory.APPRECIATION_GROWTH);
		
	}
	
	private JournalEntry createJournalEntryForExpenseSaleCommission(final YearMonth yearMonth, BalanceTracker balanceTracker) {
		Optional<ResourceParamDateValue<?>> saleCommissionOpt = getResource().getResourceParamDateValue(ResourceParamNameEnum.PROPERTY_ASSET_SALE_COMMISSION, yearMonth);
		BigDecimal saleCommission = (BigDecimal) saleCommissionOpt.get().getValue();
		saleCommission = saleCommission.divide(BigDecimal.valueOf(100));
		BigDecimal amount = BigDecimal.valueOf(balanceTracker.getResourceFixedAmount(getResource())).multiply(saleCommission);
		return JournalEntryFactory.create(getResource(), yearMonth, -integerOf(amount), CashflowCategory.EXPENSE_ASSET_SALE_COMMISSION);	
	}
	
	private JournalEntry createJournalEntryForExpenseSaleFixed(final YearMonth yearMonth, BalanceTracker balanceTracker) {
		Optional<ResourceParamDateValue<?>> saleFixedOpt = getResource().getResourceParamDateValue(ResourceParamNameEnum.PROPERTY_ASSET_SALE_FIXED, yearMonth);
		Integer saleCommission = (Integer) saleFixedOpt.get().getValue();
		return JournalEntryFactory.create(getResource(), yearMonth, -saleCommission, CashflowCategory.EXPENSE_ASSET_SALE_FIXED);	
	}
	
	private BigDecimal propertyGrowthRate(final YearMonth yearMonth) {
		// Return the local growth rate if it exists.
		Optional<ResourceParamDateValue<?>> growthRateOpt = getResource().getResourceParamDateValue(ResourceParamNameEnum.PROPERTY_HOUSING_MARKET_GROWTH_RATE, yearMonth);
		if (growthRateOpt.isPresent()) {
			return (BigDecimal) growthRateOpt.get().getValue();
		}
		// Return the scenario growth rate otherwise.
		growthRateOpt = getResource().getScenario().getResourceScenario().getResourceParamDateValue(ResourceParamNameEnum.SCENARIO_HOUSING_MARKET_GROWTH_RATE, yearMonth);
		return (BigDecimal) growthRateOpt.get().getValue();
	}
}