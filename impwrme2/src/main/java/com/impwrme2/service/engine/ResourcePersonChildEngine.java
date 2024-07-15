package com.impwrme2.service.engine;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import com.impwrme2.model.cashflow.Cashflow;
import com.impwrme2.model.journalEntry.JournalEntry;
import com.impwrme2.model.milestone.MilestoneType;
import com.impwrme2.model.resource.ResourcePersonChild;
import com.impwrme2.model.resource.enums.ResourceParamNameEnum;
import com.impwrme2.service.journalEntry.BalanceTracker;

public class ResourcePersonChildEngine extends ResourceEngine {

	private final YearMonth birthYearMonth;
	private final Integer departureAge;
	private final YearMonth departureDate;

	public ResourcePersonChildEngine(ResourcePersonChild resource, final BalanceTracker balanceTracker) {
		super(resource, balanceTracker);
		birthYearMonth = (YearMonth)  resource.getResourceParamDateValue(ResourceParamNameEnum.PERSON_BIRTH_YEAR_MONTH, resource.getStartYearMonth()).get().getValue();
		departureAge = (Integer)  resource.getResourceParamDateValue(ResourceParamNameEnum.PERSON_DEPARTURE_AGE, resource.getStartYearMonth()).get().getValue();
		departureDate = birthYearMonth.plusYears(departureAge);
	}

	@Override
	public Integer getBalanceLiquidLegalMaxIfNotSpecified(final BalanceTracker balanceTracker) {
		return Integer.valueOf(0);
	}

	@Override
	public Integer getBalanceLiquidLegalMinIfNotSpecified() {
		return Integer.valueOf(0);
	}

	@Override
	public List<JournalEntry> generateJournalEntries(YearMonth yearMonth, BalanceTracker balanceTracker) {
		if (yearMonth.equals(departureDate)) {
			addMilestone(yearMonth, MilestoneType.PERSON_DEPARTURE_AGE, getResource().getName(), String.valueOf(departureAge));
		}
		final List<Cashflow> cashflowsToProcess = new ArrayList<Cashflow>(getResource().getCashflows());
		cashflowsToProcess.removeIf(value -> departureDateReached(yearMonth));
		return generateJournalEntriesFromCashflows(yearMonth, cashflowsToProcess);

	}
		
	private boolean departureDateReached(final YearMonth yearMonth) {
		if (!yearMonth.isBefore(departureDate)) {
			return true;
		}
		return false;
	}
}
