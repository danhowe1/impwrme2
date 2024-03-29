package com.impwrme2.service.engine;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import com.impwrme2.model.cashflow.Cashflow;
import com.impwrme2.model.cashflow.CashflowCategory;
import com.impwrme2.model.resource.ResourcePerson;
import com.impwrme2.model.resource.enums.ResourceParamNameEnum;

public class ResourcePersonEngine extends ResourceEngine {

	private final YearMonth birthYearMonth;
	private final Integer retirementAge;
	private final YearMonth retirementDate;
	
	public ResourcePersonEngine(ResourcePerson resource) {
		super(resource);
		birthYearMonth = (YearMonth)  resource.getResourceParamDateValue(ResourceParamNameEnum.PERSON_BIRTH_YEAR_MONTH).get().getValue();
		retirementAge = (Integer)  resource.getResourceParamDateValue(ResourceParamNameEnum.PERSON_RETIREMENT_AGE).get().getValue();
		retirementDate = birthYearMonth.plusYears(retirementAge);
	}

	@Override
	public List<Cashflow> getCashflowsToProcess(final YearMonth yearMonth) {
		final List<Cashflow> cashflowsToProcess = new ArrayList<Cashflow>(getResource().getCashflows());
		cashflowsToProcess.removeIf(value -> retirementDateReached(yearMonth) && employmentStartDateIsBeforeRetirementAge(value, yearMonth));
		
		if (yearMonth.isAfter(YearMonth.of(2039, 11))) {
			System.out.println(yearMonth.toString());
			for (Cashflow cf : cashflowsToProcess) {
				System.out.println(cf.getCategory().getValue());
			}
		}		
		return cashflowsToProcess;
	}
	
	private boolean retirementDateReached(final YearMonth yearMonth) {
		if (!yearMonth.isBefore(retirementDate)) {
			return true;
		}
		return false;
	}
	
	private boolean employmentStartDateIsBeforeRetirementAge(final Cashflow cashflow, final YearMonth yearMonth) {
		if (cashflow.getCategory().equals(CashflowCategory.INCOME_EMPLOYMENT)) {
			YearMonth employmentStartDate = cashflow.getCashflowDateRangeValue(yearMonth).get().getYearMonthStart();
			if (employmentStartDate.isBefore(retirementDate)) {
				return true;
			}
		}
		return false;
	}	
}
