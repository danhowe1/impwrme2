package com.impwrme2.service.engine;

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

	@Override
	public List<Cashflow> getCashflowsToProcess(final YearMonth yearMonth) {
		final List<Cashflow> cashflowsToProcess = new ArrayList<Cashflow>(getResource().getCashflows());
		
		ResourceParamDateValue<String> status = (ResourceParamDateValue<String>) getResource().getResourceParamDateValue(ResourceParamNameEnum.PROPERTY_STATUS, yearMonth).get();
		
//		cashflowsToProcess.removeIf(value -> retirementDateReached(yearMonth) && employmentStartDateIsBeforeRetirementAge(value, yearMonth));
		return cashflowsToProcess;
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