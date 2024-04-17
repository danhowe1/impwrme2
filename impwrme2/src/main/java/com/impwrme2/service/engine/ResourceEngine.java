package com.impwrme2.service.engine;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import com.impwrme2.model.cashflow.Cashflow;
import com.impwrme2.model.resource.Resource;
import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.model.resourceParam.ResourceParam;
import com.impwrme2.model.resourceParamDateValue.ResourceParamDateValue;

public abstract class ResourceEngine implements IResourceEngine {

	protected final Resource resource;
	private Optional<Integer> balanceLiquidLegalMaxConstant = Optional.empty();
	private Optional<Integer> balanceLiquidLegalMinConstant = Optional.empty();
	private Optional<Integer> balanceLiquidPreferredMaxConstant = Optional.empty();
	private Optional<Integer> balanceLiquidPreferredMinConstant = Optional.empty();
	
	/**
	 * No-args constructor required for Spring instantiation.
	 */
	protected ResourceEngine() {
		this.resource = null;
	}

	public ResourceEngine(final Resource resource) {
		this.resource = resource;
		
		// Set the initial balances because the get methods for these may not get called in month 1 and therefore never be set.
		
		Optional<ResourceParam<?>> rpLegalMaxOpt = resource.getResourceParam(ResourceParamNameEnum.BALANCE_LIQUID_LEGAL_MAX);
		if (rpLegalMaxOpt.isEmpty()) {
			// No resource param found so use the default one.
			balanceLiquidLegalMaxConstant =  Optional.of(getBalanceLiquidLegalMaxIfNotSpecified());			
		} else {
			List<?> resourceParamDateValues = rpLegalMaxOpt.get().getResourceParamDateValues();
			if (1 == resourceParamDateValues.size()) {
				ResourceParamDateValue<?> rpdv = rpLegalMaxOpt.get().getResourceParamDateValues().get(0);
				if (rpdv.getYearMonth().equals(resource.getStartYearMonth())) {
					// Only 1 date/value pair exists and it starts from the resource start date so won't change.
					balanceLiquidLegalMaxConstant = Optional.of((Integer)rpdv.getValue());
				}
			}
		}

		Optional<ResourceParam<?>> rpLegalMinOpt = resource.getResourceParam(ResourceParamNameEnum.BALANCE_LIQUID_LEGAL_MIN);
		if (rpLegalMinOpt.isEmpty()) {
			// No resource param found so use the default one.
			balanceLiquidLegalMinConstant =  Optional.of(getBalanceLiquidLegalMinIfNotSpecified());			
		} else {
			List<?> resourceParamDateValues = rpLegalMinOpt.get().getResourceParamDateValues();
			if (1 == resourceParamDateValues.size()) {
				ResourceParamDateValue<?> rpdv = rpLegalMinOpt.get().getResourceParamDateValues().get(0);
				if (rpdv.getYearMonth().equals(resource.getStartYearMonth())) {
					// Only 1 date/value pair exists and it starts from the resource start date so won't change.
					balanceLiquidLegalMinConstant = Optional.of((Integer)rpdv.getValue());
				}
			}
		}

		Optional<ResourceParam<?>> rpPreferredMaxOpt = resource.getResourceParam(ResourceParamNameEnum.BALANCE_LIQUID_PREFERRED_MAX);
		if (rpPreferredMaxOpt.isEmpty()) {
			// No resource param found so use the default one.
			balanceLiquidPreferredMaxConstant =  Optional.of(getBalanceLiquidPreferredMaxIfNotSpecified());			
		} else {
			List<?> resourceParamDateValues = rpPreferredMaxOpt.get().getResourceParamDateValues();
			if (1 == resourceParamDateValues.size()) {
				ResourceParamDateValue<?> rpdv = rpPreferredMaxOpt.get().getResourceParamDateValues().get(0);
				if (rpdv.getYearMonth().equals(resource.getStartYearMonth())) {
					// Only 1 date/value pair exists and it starts from the resource start date so won't change.
					balanceLiquidPreferredMaxConstant = Optional.of((Integer)rpdv.getValue());
				}
			}
		}

		Optional<ResourceParam<?>> rpPreferredMinOpt = resource.getResourceParam(ResourceParamNameEnum.BALANCE_LIQUID_PREFERRED_MIN);
		if (rpPreferredMinOpt.isEmpty()) {
			// No resource param found so use the default one.
			balanceLiquidPreferredMinConstant =  Optional.of(getBalanceLiquidPreferredMinIfNotSpecified());			
		} else {
			List<?> resourceParamDateValues = rpPreferredMinOpt.get().getResourceParamDateValues();
			if (1 == resourceParamDateValues.size()) {
				ResourceParamDateValue<?> rpdv = rpPreferredMinOpt.get().getResourceParamDateValues().get(0);
				if (rpdv.getYearMonth().equals(resource.getStartYearMonth())) {
					// Only 1 date/value pair exists and it starts from the resource start date so won't change.
					balanceLiquidPreferredMinConstant = Optional.of((Integer)rpdv.getValue());
				}
			}
		}
		
	}

	@Override
	public Resource getResource() {
		return resource;
	}

	@Override
	public final Integer getBalanceLiquidLegalMax(YearMonth yearMonth) {
		if (balanceLiquidLegalMaxConstant.isPresent()) {
			return balanceLiquidLegalMaxConstant.get();
		}
		Optional<ResourceParamDateValue<?>> rpdvOpt = resource.getResourceParamDateValue(ResourceParamNameEnum.BALANCE_LIQUID_LEGAL_MAX, yearMonth);
		if (rpdvOpt.isPresent()) {
			return (Integer) rpdvOpt.get().getValue();
		}
		return getBalanceLiquidLegalMaxIfNotSpecified();
	}

	@Override
	public final Integer getBalanceLiquidLegalMin(YearMonth yearMonth) {
		if (balanceLiquidLegalMinConstant.isPresent()) {
			return balanceLiquidLegalMinConstant.get();
		}
		Optional<ResourceParamDateValue<?>> rpdvOpt = resource.getResourceParamDateValue(ResourceParamNameEnum.BALANCE_LIQUID_LEGAL_MIN, yearMonth);
		if (rpdvOpt.isPresent()) {
			return (Integer) rpdvOpt.get().getValue();
		}
		return getBalanceLiquidLegalMinIfNotSpecified();
	}

	@Override
	public final Integer getBalanceLiquidPreferredMax(YearMonth yearMonth) {
		if (balanceLiquidPreferredMaxConstant.isPresent()) {
			return balanceLiquidPreferredMaxConstant.get();
		}
		Optional<ResourceParamDateValue<?>> rpdvOpt = resource.getResourceParamDateValue(ResourceParamNameEnum.BALANCE_LIQUID_PREFERRED_MAX, yearMonth);
		if (rpdvOpt.isPresent()) {
			return (Integer) rpdvOpt.get().getValue();
		}
		return getBalanceLiquidPreferredMaxIfNotSpecified();
	}

	@Override
	public final Integer getBalanceLiquidPreferredMin(YearMonth yearMonth) {
		if (balanceLiquidPreferredMinConstant.isPresent()) {
			return balanceLiquidPreferredMinConstant.get();
		}
		Optional<ResourceParamDateValue<?>> rpdvOpt = resource.getResourceParamDateValue(ResourceParamNameEnum.BALANCE_LIQUID_PREFERRED_MIN, yearMonth);
		if (rpdvOpt.isPresent()) {
			return (Integer) rpdvOpt.get().getValue();
		}
		return getBalanceLiquidPreferredMinIfNotSpecified();
	}

	@Override
	public int compareTo(IResourceEngine o) {
		return this.getResource().compareTo(o.getResource());
	}

	@Override
	public List<Cashflow> getCashflowsToProcess(final YearMonth yearMonth) {
		return getResource().getCashflows();
	}
}
