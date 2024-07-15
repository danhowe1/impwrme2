package com.impwrme2.service.engine;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.impwrme2.model.cashflow.Cashflow;
import com.impwrme2.model.cashflow.CashflowCategory;
import com.impwrme2.model.cashflowDateRangeValue.CashflowDateRangeValue;
import com.impwrme2.model.journalEntry.JournalEntry;
import com.impwrme2.model.milestone.MilestoneType;
import com.impwrme2.model.resource.ResourcePersonAdult;
import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.service.journalEntry.BalanceTracker;

public class ResourcePersonAdultEngine extends ResourceEngine {

	private final YearMonth birthYearMonth;
	private final Integer retirementAge;
	private final YearMonth retirementDate;
	
	public ResourcePersonAdultEngine(ResourcePersonAdult resource, final BalanceTracker balanceTracker) {
		super(resource, balanceTracker);
		birthYearMonth = (YearMonth)  resource.getResourceParamDateValue(ResourceParamNameEnum.PERSON_BIRTH_YEAR_MONTH, resource.getStartYearMonth()).get().getValue();
		retirementAge = (Integer)  resource.getResourceParamDateValue(ResourceParamNameEnum.PERSON_ADULT_RETIREMENT_AGE, resource.getStartYearMonth()).get().getValue();
		retirementDate = birthYearMonth.plusYears(retirementAge);
	}

	@Override
	public Integer getBalanceLiquidLegalMaxIfNotSpecified(final BalanceTracker balanceTracker) {
		return Integer.valueOf(0);
	}

	@Override
	public Integer getBalanceLiquidLegalMinIfNotSpecified() {
		return Integer.valueOf(0);
	}

	@Override
	public List<JournalEntry> generateJournalEntries(YearMonth yearMonth, BalanceTracker balanceTracker) {
		if (yearMonth.equals(retirementDate)) {
			addMilestone(yearMonth, MilestoneType.PERSON_RETIREMENT_AGE, getResource().getName(), String.valueOf(retirementAge));
		}
		final List<Cashflow> cashflowsToProcess = new ArrayList<Cashflow>(getResource().getCashflows());
		cashflowsToProcess.removeIf(value -> retirementDateReached(yearMonth) && employmentStartDateIsBeforeRetirementAge(value, yearMonth));
		return generateJournalEntriesFromCashflows(yearMonth, cashflowsToProcess);

	}
		
	private boolean retirementDateReached(final YearMonth yearMonth) {
		if (!yearMonth.isBefore(retirementDate)) {
			return true;
		}
		return false;
	}
	
	private boolean employmentStartDateIsBeforeRetirementAge(final Cashflow cashflow, final YearMonth yearMonth) {
		if (cashflow.getCategory().equals(CashflowCategory.INCOME_EMPLOYMENT)) {
			Optional<CashflowDateRangeValue> empCfdrv = cashflow.getCashflowDateRangeValue(yearMonth);
			if (empCfdrv.isPresent()) { 
				YearMonth employmentStartDate = empCfdrv.get().getYearMonthStart();
				if (employmentStartDate.isBefore(retirementDate)) {
					return true;
				}
			}
		}
		return false;
	}	
}
