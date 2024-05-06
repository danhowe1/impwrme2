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
	private boolean payOutDateReached = false;
	
	public ResourceMortgageEngine(final ResourceMortgageExisting resource, final BalanceTracker balanceTracker) {
		super(resource, balanceTracker);
		balanceOpeningFixed = (Integer)  resource.getResourceParamDateValue(ResourceParamNameEnum.BALANCE_OPENING_FIXED, resource.getStartYearMonth()).get().getValue();
		Integer monthsRemaining = (Integer)  resource.getResourceParamDateValue(ResourceParamNameEnum.MORTGAGE_REMAINING_MONTHS, resource.getStartYearMonth()).get().getValue();
		Integer yearsRemaining = (Integer)  resource.getResourceParamDateValue(ResourceParamNameEnum.MORTGAGE_REMAINING_YEARS, resource.getStartYearMonth()).get().getValue();
		totalMonthsRemaining = yearsRemaining * 12 + monthsRemaining;
	}

	@Override
	public Integer getBalanceLiquidLegalMaxIfNotSpecified(final BalanceTracker balanceTracker) {
		return -1 * balanceTracker.getResourceFixedAmount(getResource());
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

	@Override
	public List<JournalEntry> generateJournalEntries(YearMonth yearMonth, BalanceTracker balanceTracker) {

		List<JournalEntry> journalEntries = new ArrayList<JournalEntry>();
		if (yearMonth.isBefore(getResource().getStartYearMonth())) return journalEntries;
		if (payOutDateReached) return journalEntries;

		ResourceParamDateValue<String> mortgageType = mortgageType(yearMonth);
		if (isPropertySaleMonth(yearMonth) || isPayOutMonth(mortgageType, yearMonth)) {
			payOutDateReached = true;
		}
		
		if (mortgageType.getValue().equals(ResourceMortgageExisting.MORTGAGE_REPAYMENT_INTEREST_ONLY)) {
			// Alter the months remaining for interest only loans.
			totalMonthsRemaining--;			
			if (totalMonthsRemaining == 0) {
				// Last month so pay out the mortgage.
				payOutDateReached = true;
			}
		}
		
		journalEntries.addAll(generateJournalEntriesForCurrentMonth(yearMonth, balanceTracker));

		// Standard cash-flows.
		if (!payOutDateReached) {
			journalEntries.addAll(generateJournalEntriesFromCashflows(yearMonth, resource.getCashflows()));
		}
		
		return journalEntries;
	}

	private boolean isPropertySaleMonth(final YearMonth yearMonth) {
		ResourceParamDateValue<String> propertyStatus = propertyStatus(yearMonth);
		if (propertyStatus.getValue().equals(ResourcePropertyExisting.PROPERTY_STATUS_SOLD) &&
			propertyStatus.getYearMonth().equals(yearMonth)) {
			payOutDateReached = true;
			return true;
		}
		return false;
	}

	private List<JournalEntry> generateJournalEntriesForCurrentMonth(final YearMonth yearMonth, final BalanceTracker balanceTracker) {
		List<JournalEntry> journalEntries = new ArrayList<JournalEntry>();
		ResourceParamDateValue<String> mortgageType = mortgageType(yearMonth);

		Integer interestExpense = calculateInterestPayment(balanceTracker, yearMonth);
		Integer principalExpense = 0;
		Integer additionalPayments = balanceTracker.getResourceLiquidDepositAmount(resource);
		
		if (payOutDateReached) {
			principalExpense = balanceTracker.getResourceFixedAmount(resource);
			if (additionalPayments > 0) {
				journalEntries.add(JournalEntryFactory.create(getResource(), yearMonth, -additionalPayments, CashflowCategory.WITHDRAWAL_BALANCE));
			}
		} else if (mortgageType.getValue().equals(ResourceMortgageExisting.MORTGAGE_REPAYMENT_PRINCIPAL_AND_INTEREST)) {
			Integer principalAndInterestPreOffset = calculatePrincipalAndInterestPreOffset(yearMonth);
			principalExpense = principalAndInterestPreOffset - interestExpense;
			
			if (principalExpense < balanceTracker.getResourceAssetValue(resource)) {
				principalExpense = balanceTracker.getResourceAssetValue(resource);
				payOutDateReached = true;
				if (additionalPayments > 0) {
					principalExpense = principalExpense - additionalPayments;
					journalEntries.add(JournalEntryFactory.create(getResource(), yearMonth, -additionalPayments, CashflowCategory.WITHDRAWAL_BALANCE));
				}
			}
		}
		
		if (principalExpense < 0) {
			journalEntries.add(JournalEntryFactory.create(getResource(), yearMonth, principalExpense, CashflowCategory.EXPENSE_PRINCIPAL));
			journalEntries.add(JournalEntryFactory.create(getResource(), yearMonth, -principalExpense, CashflowCategory.APPRECIATION_GROWTH_FIXED));
		}
		
		if (interestExpense < 0 && !payOutDateReached) {
			journalEntries.add(JournalEntryFactory.create(getResource(), yearMonth, interestExpense, CashflowCategory.EXPENSE_INTEREST));
		}
		
		return journalEntries;
	}
	
	@SuppressWarnings("unchecked")
	private ResourceParamDateValue<String> propertyStatus(final YearMonth yearMonth) {
		Optional<ResourceParamDateValue<?>> propertyStatusOpt = (Optional<ResourceParamDateValue<?>>) getResource().getParent().getResourceParamDateValue(ResourceParamNameEnum.PROPERTY_STATUS, yearMonth);
		return (ResourceParamDateValue<String>) propertyStatusOpt.get();
	}

	private boolean isPayOutMonth(final ResourceParamDateValue<String> mortgageType, final YearMonth yearMonth) {
		if (mortgageType.getValue().equals(ResourceMortgageExisting.MORTGAGE_REPAYMENT_PAY_OUT) &&
			mortgageType.getYearMonth().equals(yearMonth)) {
			payOutDateReached = true;
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private ResourceParamDateValue<String> mortgageType(final YearMonth yearMonth) {
		Optional<ResourceParamDateValue<?>> mortgageTypeOpt = (Optional<ResourceParamDateValue<?>>) getResource().getResourceParamDateValue(ResourceParamNameEnum.MORTGAGE_REPAYMENT_TYPE, yearMonth);
		return (ResourceParamDateValue<String>) mortgageTypeOpt.get();
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

	private Integer calculateInterestPayment(final BalanceTracker balanceTracker, final YearMonth yearMonth) {
		BigDecimal interestOnlyPreOffset = calculateInterestOnlyPreOffset(balanceTracker, yearMonth);
		BigDecimal interestEarnedInOffsetAccounts = calculateInterestEarnedInOffsetAccounts(balanceTracker, yearMonth);
		return Math.min(0, integerOf(interestOnlyPreOffset.add(interestEarnedInOffsetAccounts)));
	}
	
	private BigDecimal calculateInterestOnlyPreOffset(final BalanceTracker balanceTracker, final YearMonth yearMonth) {
		BigDecimal monthlyInterestRate = monthlyInterestRate(yearMonth);
		// We work on the previous months balance so that the offset accounts can net off properly.
		BigDecimal balance = new BigDecimal(balanceTracker.getResourceFixedAmountPreviousMonth(resource) + balanceTracker.getResourceLiquidDepositAmountPreviousMonth(resource));
		return monthlyInterestRate.multiply(balance);
	}

	private BigDecimal calculateInterestEarnedInOffsetAccounts(final BalanceTracker balanceTracker, final YearMonth yearMonth) {
		BigDecimal monthlyInterestRate = monthlyInterestRate(yearMonth);
		BigDecimal aggregateOffsetBalance = BigDecimal.ZERO;
		for (Resource childResource : resource.getChildren()) {
			aggregateOffsetBalance = aggregateOffsetBalance.add(new BigDecimal(balanceTracker.getResourceLiquidBalancePreviousMonth(childResource)));
		}
		return monthlyInterestRate.multiply(aggregateOffsetBalance);
	}
}
