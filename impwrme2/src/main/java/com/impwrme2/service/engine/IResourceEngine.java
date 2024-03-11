package com.impwrme2.service.engine;

import java.math.BigDecimal;
import java.time.YearMonth;

import com.impwrme2.model.resource.Resource;

public interface IResourceEngine {

	public Resource getResource();
	public BigDecimal getBalanceLiquidLegalMax(final YearMonth yearMonth);
	public BigDecimal getBalanceLiquidLegalMin(final YearMonth yearMonth);
	public BigDecimal getBalanceLiquidPreferredMax(final YearMonth yearMonth);
	public BigDecimal getBalanceLiquidPreferredMin(final YearMonth yearMonth);
//	public List<JournalEntry> generateJournalEntriesForExpensesIncomeAndWithdrawals(final PotManagerService potManager);
}
