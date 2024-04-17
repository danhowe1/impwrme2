package com.impwrme2.service.engine;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import com.impwrme2.model.cashflow.Cashflow;
import com.impwrme2.model.resource.ResourcePropertyExisting;
import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValue;

public class ResourcePropertyExistingEngine extends ResourceEngine {

	public ResourcePropertyExistingEngine(ResourcePropertyExisting resource) {
		super(resource);
	}

	private boolean isSold = false;
	
	@Override
	public List<Cashflow> getCashflowsToProcess(final YearMonth yearMonth) {

		if (isSold) return new ArrayList<Cashflow>();

		@SuppressWarnings("unchecked")
		ResourceParamDateValue<String> status = (ResourceParamDateValue<String>) getResource().getResourceParamDateValue(ResourceParamNameEnum.PROPERTY_STATUS, yearMonth).get();
		
		if (isSaleMonth(status, yearMonth)) {
			return new ArrayList<Cashflow>();
		}

		final List<Cashflow> cashflowsToProcess = new ArrayList<Cashflow>(getResource().getCashflows());
		
//		cashflowsToProcess.removeIf(value -> retirementDateReached(yearMonth) && employmentStartDateIsBeforeRetirementAge(value, yearMonth));
		return cashflowsToProcess;
	}

	private boolean isSaleMonth(final ResourceParamDateValue<String> status, final YearMonth yearMonth) {
		if (status.getResourceParam().getName().equals(ResourceParamNameEnum.PROPERTY_STATUS) &&
			status.getYearMonth().equals(yearMonth)) {
			isSold = true;
			return true;
		}
		return false;
	}

	private BigDecimal propertyGrowthRate() {
		return null;
	}
	
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
}