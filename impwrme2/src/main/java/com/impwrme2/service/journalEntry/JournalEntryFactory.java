package com.impwrme2.service.journalEntry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;

import org.springframework.stereotype.Component;

import com.impwrme2.model.cashflow.CashflowCategory;
import com.impwrme2.model.journalEntry.JournalEntry;
import com.impwrme2.model.resource.Resource;

@Component
public class JournalEntryFactory {

	public JournalEntry create(Resource resource, YearMonth yearMonth, Integer amount, CashflowCategory category) {
		return create(resource, yearMonth, amount, category, null);
	}

	public JournalEntry create(Resource resource, YearMonth yearMonth, Integer amount, CashflowCategory category, String detail) {
		return new JournalEntry(resource, yearMonth, category, amount);
	}

//	private Integer integerOf(BigDecimal decimal) {
//		return decimal.setScale(0, RoundingMode.HALF_UP).intValue();
//	}
}
