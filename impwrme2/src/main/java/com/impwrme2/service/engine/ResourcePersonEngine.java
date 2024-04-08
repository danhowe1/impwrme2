package com.impwrme2.service.engine;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.impwrme2.model.cashflow.Cashflow;
import com.impwrme2.model.cashflow.CashflowCategory;
import com.impwrme2.model.cashflowDateRangeValue.CashflowDateRangeValue;
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
	public Integer getBalanceLiquidLegalMax(YearMonth yearMonth) {
		return Integer.valueOf(0);
	}

	@Override
	public Integer getBalanceLiquidLegalMin(YearMonth yearMonth) {
		return Integer.valueOf(0);
	}

	@Override
	public Integer getBalanceLiquidPreferredMax(YearMonth yearMonth) {
		return Integer.valueOf(0);
	}

	@Override
	public Integer getBalanceLiquidPreferredMin(YearMonth yearMonth) {
		return Integer.valueOf(0);
	}

	@Override
	public List<Cashflow> getCashflowsToProcess(final YearMonth yearMonth) {
		final List<Cashflow> cashflowsToProcess = new ArrayList<Cashflow>(getResource().getCashflows());
		cashflowsToProcess.removeIf(value -> retirementDateReached(yearMonth) && employmentStartDateIsBeforeRetirementAge(value, yearMonth));
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
