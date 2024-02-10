package com.impwrme2.service.engine;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import com.impwrme2.model.journalEntry.JournalEntry;
import com.impwrme2.model.resource.Resource;
import com.impwrme2.service.journalEntry.PotManagerService;

public interface IResourceEngine {

	public Resource getResource();
//	public BigDecimal getBalanceLiquidLegalMax(final PotManagerService potManager);
	public BigDecimal getBalanceLiquidLegalMax(final YearMonth yearMonth);
	public BigDecimal getBalanceLiquidLegalMin(final YearMonth yearMonth);
	public BigDecimal getBalanceLiquidPreferred(final YearMonth yearMonth);
	public List<JournalEntry> generateJournalEntriesForExpensesIncomeAndWithdrawals(final PotManagerService potManager);
}
