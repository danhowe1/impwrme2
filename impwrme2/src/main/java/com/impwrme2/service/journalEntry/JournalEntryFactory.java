package com.impwrme2.service.journalEntry;

import java.math.BigDecimal;
import java.time.YearMonth;

import com.impwrme2.model.cashflow.CashflowCategory;
import com.impwrme2.model.journalEntry.JournalEntry;
import com.impwrme2.model.resource.Resource;

public class JournalEntryFactory {

	public static JournalEntry createBalanceOpeningLiquid(Resource resource, YearMonth yearMonth, BigDecimal amount) {
		return new JournalEntry(resource, yearMonth, CashflowCategory.JE_BALANCE_OPENING_LIQUID, amount);
	}

	public static JournalEntry createBalanceOpeningAssetValue(Resource resource, YearMonth yearMonth, BigDecimal amount) {
		return new JournalEntry(resource, yearMonth, CashflowCategory.JE_BALANCE_OPENING_ASSET_VALUE, amount);
	}

	public static JournalEntry create(Resource resource, YearMonth yearMonth, BigDecimal amount, CashflowCategory category) {
		return new JournalEntry(resource, yearMonth, category, amount);
	}
}
