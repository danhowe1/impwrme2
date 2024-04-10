package com.impwrme2.service.engine;

import java.time.YearMonth;
import java.util.List;

import com.impwrme2.model.cashflow.Cashflow;
import com.impwrme2.model.resource.Resource;

public interface IResourceEngine extends Comparable<IResourceEngine> {

	public Resource getResource();
	public Integer getBalanceLiquidLegalMax(final YearMonth yearMonth);
	public Integer getBalanceLiquidLegalMaxIfNotSpecified();
	public Integer getBalanceLiquidLegalMin(final YearMonth yearMonth);
	public Integer getBalanceLiquidLegalMinIfNotSpecified();
	public Integer getBalanceLiquidPreferredMax(final YearMonth yearMonth);
	public Integer getBalanceLiquidPreferredMaxIfNotSpecified();
	public Integer getBalanceLiquidPreferredMin(final YearMonth yearMonth);
	public Integer getBalanceLiquidPreferredMinIfNotSpecified();
	public List<Cashflow> getCashflowsToProcess(final YearMonth yearMonth);
}
