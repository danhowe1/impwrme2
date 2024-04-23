package com.impwrme2.service.journalEntry;

import java.time.YearMonth;

import org.springframework.stereotype.Component;

import com.impwrme2.model.cashflow.CashflowCategory;
import com.impwrme2.model.journalEntry.JournalEntry;
import com.impwrme2.model.resource.Resource;

@Component
public class JournalEntryFactory {

	public static JournalEntry create(Resource resource, YearMonth yearMonth, Integer amount, CashflowCategory category) {
		return create(resource, yearMonth, amount, category, null);
	}

	public static JournalEntry create(Resource resource, YearMonth yearMonth, Integer amount, CashflowCategory category, String detail) {
		return new JournalEntry(resource, yearMonth, category, amount);
	}
}
