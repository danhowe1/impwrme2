package com.impwrme2.service.engine;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import com.impwrme2.model.cashflow.Cashflow;
import com.impwrme2.model.resource.Resource;
import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValue;

public class ResourceEngine implements IResourceEngine {

	protected final Resource resource;
	private Integer balanceLiquidLegalMax = 0;
	private Integer balanceLiquidLegalMin = 0;
	
	/**
	 * No-args constructor required for Spring instantiation.
	 */
	protected ResourceEngine() {
		this.resource = null;
	}

	public ResourceEngine(final Resource resource) {
		this.resource = resource;
	}

	@Override
	public Resource getResource() {
		return resource;
	}

	@Override
	public Integer getBalanceLiquidLegalMax(YearMonth yearMonth) {
		Optional<ResourceParamDateValue<?>> rpdvOpt = resource.getResourceParamDateValue(ResourceParamNameEnum.BALANCE_LIQUID_LEGAL_MAX, yearMonth);
		if (rpdvOpt.isPresent()) {
			balanceLiquidLegalMax = (Integer) rpdvOpt.get().getValue();
		}
		return balanceLiquidLegalMax;
	}

	@Override
	public Integer getBalanceLiquidLegalMin(YearMonth yearMonth) {
		Optional<ResourceParamDateValue<?>> rpdvOpt = resource.getResourceParamDateValue(ResourceParamNameEnum.BALANCE_LIQUID_LEGAL_MIN, yearMonth);
		if (rpdvOpt.isPresent()) {
			balanceLiquidLegalMin = (Integer) rpdvOpt.get().getValue();
		}
		return balanceLiquidLegalMin;
	}

	@Override
	public Integer getBalanceLiquidPreferredMax(YearMonth yearMonth) {
		return getBalanceLiquidLegalMax(yearMonth);
	}

	@Override
	public Integer getBalanceLiquidPreferredMin(YearMonth yearMonth) {
		return 0;
	}

	@Override
	public int compareTo(IResourceEngine o) {
		return this.getResource().compareTo(o.getResource());
	}

	@Override
	public List<Cashflow> getCashflowsToProcess(YearMonth yearMonth) {
		return getResource().getCashflows();
	}
}
