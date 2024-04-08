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
	private Integer balanceLiquidLegalMax = null;
	private Integer balanceLiquidLegalMin = null;
	private Integer balanceLiquidPreferredMax = null;
	private Integer balanceLiquidPreferredMin = null;
	
	/**
	 * No-args constructor required for Spring instantiation.
	 */
	protected ResourceEngine() {
		this.resource = null;
	}

	public ResourceEngine(final Resource resource) {
		this.resource = resource;
		
		// Set the initial balances because the get methods for these may not get called in month 1 and therefore never be set.
		Optional<ResourceParamDateValue<?>> balanceLiquidLegalMaxOpt = resource.getResourceParamDateValue(ResourceParamNameEnum.BALANCE_LIQUID_LEGAL_MAX, resource.getStartYearMonth());
		if (balanceLiquidLegalMaxOpt.isPresent()) {
			balanceLiquidLegalMax = (Integer) balanceLiquidLegalMaxOpt.get().getValue();
		}
		Optional<ResourceParamDateValue<?>> balanceLiquidLegalMinOpt = resource.getResourceParamDateValue(ResourceParamNameEnum.BALANCE_LIQUID_LEGAL_MIN, resource.getStartYearMonth());
		if (balanceLiquidLegalMinOpt.isPresent()) {
			balanceLiquidLegalMin = (Integer) balanceLiquidLegalMinOpt.get().getValue();
		}
		Optional<ResourceParamDateValue<?>> balanceLiquidPreferredMaxOpt = resource.getResourceParamDateValue(ResourceParamNameEnum.BALANCE_LIQUID_PREFERRED_MAX, resource.getStartYearMonth());
		if (balanceLiquidPreferredMaxOpt.isPresent()) {
			balanceLiquidPreferredMax = (Integer) balanceLiquidPreferredMaxOpt.get().getValue();
		}
		Optional<ResourceParamDateValue<?>> balanceLiquidPreferredMinOpt = resource.getResourceParamDateValue(ResourceParamNameEnum.BALANCE_LIQUID_PREFERRED_MIN, resource.getStartYearMonth());
		if (balanceLiquidPreferredMinOpt.isPresent()) {
			balanceLiquidPreferredMin = (Integer) balanceLiquidPreferredMinOpt.get().getValue();
		}
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
		Optional<ResourceParamDateValue<?>> rpdvOpt = resource.getResourceParamDateValue(ResourceParamNameEnum.BALANCE_LIQUID_PREFERRED_MAX, yearMonth);
		if (rpdvOpt.isPresent()) {
			balanceLiquidPreferredMax = (Integer) rpdvOpt.get().getValue();
		}
		return balanceLiquidPreferredMax;
	}

	@Override
	public Integer getBalanceLiquidPreferredMin(YearMonth yearMonth) {
		Optional<ResourceParamDateValue<?>> rpdvOpt = resource.getResourceParamDateValue(ResourceParamNameEnum.BALANCE_LIQUID_PREFERRED_MIN, yearMonth);
		if (rpdvOpt.isPresent()) {
			balanceLiquidPreferredMin = (Integer) rpdvOpt.get().getValue();
		}
		return balanceLiquidPreferredMin;
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
