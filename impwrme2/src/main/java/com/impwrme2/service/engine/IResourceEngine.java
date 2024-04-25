package com.impwrme2.service.engine;

import java.time.YearMonth;
import java.util.List;

import com.impwrme2.model.journalEntry.JournalEntry;
import com.impwrme2.model.resource.Resource;
import com.impwrme2.service.journalEntry.BalanceTracker;

public interface IResourceEngine extends Comparable<IResourceEngine> {

	public Resource getResource();
	public Integer getBalanceLiquidLegalMax(final YearMonth yearMonth, final BalanceTracker balanceTracker);
	public Integer getBalanceLiquidLegalMaxIfNotSpecified(final BalanceTracker balanceTracker);
	public Integer getBalanceLiquidLegalMin(final YearMonth yearMonth);
	public Integer getBalanceLiquidLegalMinIfNotSpecified();
	public Integer getBalanceLiquidPreferredMax(final YearMonth yearMonth, final BalanceTracker balanceTracker);
	public Integer getBalanceLiquidPreferredMaxIfNotSpecified(final BalanceTracker balanceTracker);
	public Integer getBalanceLiquidPreferredMin(final YearMonth yearMonth);
	public Integer getBalanceLiquidPreferredMinIfNotSpecified();
//	public List<Cashflow> getCashflowsToProcess(final YearMonth yearMonth);
	public List<JournalEntry> generateJournalEntries(final YearMonth yearMonth, final BalanceTracker balanceTracker);
}
