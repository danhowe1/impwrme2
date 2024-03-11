package com.impwrme2.service.engine;

import java.time.YearMonth;

import com.impwrme2.model.resource.Resource;

public interface IResourceEngine {

	public Resource getResource();
	public Integer getBalanceLiquidLegalMax(final YearMonth yearMonth);
	public Integer getBalanceLiquidLegalMin(final YearMonth yearMonth);
	public Integer getBalanceLiquidPreferredMax(final YearMonth yearMonth);
	public Integer getBalanceLiquidPreferredMin(final YearMonth yearMonth);
//	public List<JournalEntry> generateJournalEntriesForExpensesIncomeAndWithdrawals(final PotManagerService potManager);
}
