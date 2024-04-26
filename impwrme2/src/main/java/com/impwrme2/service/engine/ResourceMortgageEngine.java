package com.impwrme2.service.engine;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.impwrme2.model.cashflow.CashflowCategory;
import com.impwrme2.model.journalEntry.JournalEntry;
import com.impwrme2.model.resource.Resource;
import com.impwrme2.model.resource.ResourceMortgageExisting;
import com.impwrme2.model.resource.ResourcePropertyExisting;
import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValue;
import com.impwrme2.service.journalEntry.BalanceTracker;
import com.impwrme2.service.journalEntry.JournalEntryFactory;

public class ResourceMortgageEngine extends ResourceEngine {

	private Integer balanceOpeningFixed;
	private Integer totalMonthsRemaining = 0;
	
	public ResourceMortgageEngine(final ResourceMortgageExisting resource, final BalanceTracker balanceTracker) {
		super(resource, balanceTracker);
		balanceOpeningFixed = (Integer)  resource.getResourceParamDateValue(ResourceParamNameEnum.BALANCE_OPENING_FIXED, resource.getStartYearMonth()).get().getValue();
		Integer monthsRemaining = (Integer)  resource.getResourceParamDateValue(ResourceParamNameEnum.MORTGAGE_REMAINING_MONTHS, resource.getStartYearMonth()).get().getValue();
		Integer yearsRemaining = (Integer)  resource.getResourceParamDateValue(ResourceParamNameEnum.MORTGAGE_REMAINING_YEARS, resource.getStartYearMonth()).get().getValue();
		totalMonthsRemaining = yearsRemaining * 12 + monthsRemaining;
	}

//	public ResourceMortgageEngine(final ResourceMortgageNew resource, final BalanceTracker balanceTracker) {
//		super(resource, balanceTracker);
//	}

	private boolean isPaidOut = false;

	@Override
	public Integer getBalanceLiquidLegalMaxIfNotSpecified(final BalanceTracker balanceTracker) {
//		return Integer.valueOf(0);
		return -1 * balanceTracker.getResourceFixedAmount(getResource());
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
		if (yearMonth.isBefore(getResource().getStartYearMonth())) return journalEntries;
		if (isPaidOut) return journalEntries;
		ResourceParamDateValue<String> mortgageType = mortgageType(yearMonth);

		// If property is being sold or pay out date is reached return pay out entries.
		if (isPropertySaleMonth(yearMonth) || isPayOutMonth(mortgageType, yearMonth)) {
			return createJournalEntriesForPayOut(balanceTracker, yearMonth);
		}
		
		if (mortgageType.getValue().equals(ResourceMortgageExisting.MORTGAGE_REPAYMENT_INTEREST_ONLY)) {
			// Alter the months remaining for interest only loans.
			totalMonthsRemaining--;
			
			if (totalMonthsRemaining == 0) {
				// This is an interest only mortgage but we've reached the end of time so 
				// we pay off the final month's interest and the full remaining principal.
				Integer assetValue = balanceTracker.getResourceAssetValue(resource);
				BigDecimal monthlyInterestRate = monthlyInterestRate(yearMonth);
				
				// Interest expense.
				Integer interestExpense = integerOf(monthlyInterestRate.multiply(new BigDecimal(assetValue)));
				if (interestExpense < 0) {
					journalEntries.add(JournalEntryFactory.create(getResource(), yearMonth, interestExpense, CashflowCategory.EXPENSE_INTEREST));
				}
				
				// Principal expense and asset appreciation.
				if (assetValue < 0) {
					journalEntries.add(JournalEntryFactory.create(getResource(), yearMonth, assetValue, CashflowCategory.EXPENSE_PRINCIPAL));					
					journalEntries.add(JournalEntryFactory.create(getResource(), yearMonth, -assetValue, CashflowCategory.APPRECIATION_GROWTH));					
				}
				
				return  journalEntries;
			}
		}
		
		Integer principalAndInterestPreOffset = calculatePrincipalAndInterestPreOffset(yearMonth);
		BigDecimal interestOnlyPreOffset = calculateInterestOnlyPreOffset(balanceTracker, yearMonth);
		BigDecimal interestEarnedInOffsetAccounts = calculateInterestEarnedInOffsetAccounts(balanceTracker, yearMonth);
		
		Integer interestExpense = integerOf(interestOnlyPreOffset.add(interestEarnedInOffsetAccounts));
		Integer principalExpense = 0;
		
		if (mortgageType.getValue().equals(ResourceMortgageExisting.MORTGAGE_REPAYMENT_PRINCIPAL_AND_INTEREST)) {
			principalExpense = principalAndInterestPreOffset - interestExpense;
		}
		
		if (Math.abs(principalExpense) >= Math.abs(balanceTracker.getResourceAssetValue(resource))) {
			return createJournalEntriesForPayOut(balanceTracker, yearMonth);
		}
				
		journalEntries.add(JournalEntryFactory.create(getResource(), yearMonth, interestExpense, CashflowCategory.EXPENSE_INTEREST));
		journalEntries.add(JournalEntryFactory.create(getResource(), yearMonth, principalExpense, CashflowCategory.EXPENSE_PRINCIPAL));					
		journalEntries.add(JournalEntryFactory.create(getResource(), yearMonth, -principalExpense, CashflowCategory.APPRECIATION_GROWTH));					

		// Standard cash-flows.
		journalEntries.addAll(generateJournalEntriesFromCashflows(yearMonth, resource.getCashflows()));
		
		return journalEntries;
	}

	private boolean isPropertySaleMonth(final YearMonth yearMonth) {
		ResourceParamDateValue<String> propertyStatus = propertyStatus(yearMonth);
		if (propertyStatus.getValue().equals(ResourcePropertyExisting.PROPERTY_STATUS_SOLD) &&
			propertyStatus.getYearMonth().equals(yearMonth)) {
			isPaidOut = true;
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private ResourceParamDateValue<String> propertyStatus(final YearMonth yearMonth) {
		Optional<ResourceParamDateValue<?>> propertyStatusOpt = (Optional<ResourceParamDateValue<?>>) getResource().getParent().getResourceParamDateValue(ResourceParamNameEnum.PROPERTY_STATUS, yearMonth);
		return (ResourceParamDateValue<String>) propertyStatusOpt.get();
	}

	private boolean isPayOutMonth(final ResourceParamDateValue<String> mortgageType, final YearMonth yearMonth) {
		if (mortgageType.getValue().equals(ResourceMortgageExisting.MORTGAGE_REPAYMENT_PAY_OUT) &&
			mortgageType.getYearMonth().equals(yearMonth)) {
			isPaidOut = true;
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private ResourceParamDateValue<String> mortgageType(final YearMonth yearMonth) {
		Optional<ResourceParamDateValue<?>> mortgageTypeOpt = (Optional<ResourceParamDateValue<?>>) getResource().getResourceParamDateValue(ResourceParamNameEnum.MORTGAGE_REPAYMENT_TYPE, yearMonth);
		return (ResourceParamDateValue<String>) mortgageTypeOpt.get();
	}

	private List<JournalEntry> createJournalEntriesForPayOut(final BalanceTracker balanceTracker, final YearMonth yearMonth) {
		List<JournalEntry> journalEntries = new ArrayList<JournalEntry>();
		Integer mortgageAmount = balanceTracker.getResourceFixedAmount(resource);
		Integer additionalPayments = balanceTracker.getResourceLiquidDepositAmount(resource);
		if (additionalPayments > 0) {
			journalEntries.add(JournalEntryFactory.create(getResource(), yearMonth, -additionalPayments, CashflowCategory.WITHDRAWAL_BALANCE));
		}
		journalEntries.add(JournalEntryFactory.create(getResource(), yearMonth, mortgageAmount, CashflowCategory.EXPENSE_PRINCIPAL));
		journalEntries.add(JournalEntryFactory.create(getResource(), yearMonth, -mortgageAmount, CashflowCategory.APPRECIATION_GROWTH));
		isPaidOut = true;
		
		return journalEntries;
	}

	private BigDecimal monthlyInterestRate(final YearMonth yearMonth) {
			return annualInterestRate(yearMonth).divide(BigDecimal.valueOf(1200), SCALE_10_ROUNDING_HALF_EVEN);
	}
	
	private BigDecimal annualInterestRate(final YearMonth yearMonth) {
		Optional<ResourceParamDateValue<?>> annualInterestRateOpt = (Optional<ResourceParamDateValue<?>>) getResource().getResourceParamDateValue(ResourceParamNameEnum.MORTGAGE_INTEREST_RATE, yearMonth);
		return (BigDecimal) annualInterestRateOpt.get().getValue();
	}

	private Integer calculatePrincipalAndInterestPreOffset(final YearMonth yearMonth) {

		BigDecimal monthlyInterestRate = monthlyInterestRate(yearMonth);
		BigDecimal numerator = monthlyInterestRate.multiply(new BigDecimal(balanceOpeningFixed));
		BigDecimal denominator = BigDecimal.ONE.add(monthlyInterestRate);
		denominator = denominator.pow(-totalMonthsRemaining, SCALE_10_ROUNDING_HALF_EVEN);
		denominator = BigDecimal.ONE.subtract(denominator);

		return integerOf(numerator.divide(denominator, SCALE_10_ROUNDING_HALF_EVEN));
	}

	private BigDecimal calculateInterestOnlyPreOffset(final BalanceTracker balanceTracker, final YearMonth yearMonth) {
		BigDecimal monthlyInterestRate = monthlyInterestRate(yearMonth);
		BigDecimal balance = new BigDecimal(balanceTracker.getResourceFixedAmount(resource) + balanceTracker.getResourceLiquidDepositAmount(resource));
		return monthlyInterestRate.multiply(balance);
	}

	private BigDecimal calculateInterestEarnedInOffsetAccounts(final BalanceTracker balanceTracker, final YearMonth yearMonth) {
		BigDecimal monthlyInterestRate = monthlyInterestRate(yearMonth);
		BigDecimal aggregateOffsetBalance = BigDecimal.ZERO;
		for (Resource childResource : resource.getChildren()) {
			aggregateOffsetBalance = aggregateOffsetBalance.add(new BigDecimal(balanceTracker.getResourceLiquidBalance(childResource)));
		}
		return monthlyInterestRate.multiply(aggregateOffsetBalance);
	}
}
