package com.impwrme2.service.engine;

public class ResourceMortgageEngine extends ResourceEngine {

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
		return getBalanceLiquidPreferredMaxIfNotSpecified();
	}

	@Override
	public Integer getBalanceLiquidPreferredMinIfNotSpecified() {
		return Integer.valueOf(0);
	}
}
