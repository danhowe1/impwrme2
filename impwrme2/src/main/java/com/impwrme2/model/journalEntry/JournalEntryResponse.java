package com.impwrme2.model.journalEntry;

import java.io.Serializable;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import com.impwrme2.model.milestone.Milestone;
import com.impwrme2.model.scenario.ScenarioYearBalance;

public class JournalEntryResponse implements Serializable {

	private static final long serialVersionUID = -8364314557845566906L;
	
	public JournalEntryResponse(final MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	private final MessageSource messageSource;
	
	private List<JournalEntry> journalEntries;
	
	private List<Milestone> milestones = new ArrayList<Milestone>();

	private Map<Integer, Integer> yearToLiquidBalanceMap = new HashMap<Integer, Integer>();
	
	public void amendYearToLiquidBalance(final YearMonth yearMonth, final Integer amount) {
		if (yearMonth.getMonthValue() != 12) return;
		Integer year = Integer.valueOf(yearMonth.getYear());
		Integer currentBalance = yearToLiquidBalanceMap.get(year);
		if (null == currentBalance) {
			yearToLiquidBalanceMap.put(year, amount);
		} else {
			yearToLiquidBalanceMap.put(year, currentBalance + amount);			
		}
	}

	// --------------------
	// Getters and setters.
	// --------------------
	
	public List<JournalEntry> getJournalEntries() {
		return journalEntries;
	}

	public void setJournalEntries(List<JournalEntry> journalEntries) {
		this.journalEntries = journalEntries;
	}

	public void addMilestones(List<Milestone> milestones) {
		this.milestones.addAll(milestones);
	}
	
	public List<Milestone> getMilestones() {
		return milestones;
	}
	
	public String getMilestoneMessage(YearMonth yearMonth) {
		List<Milestone> filteredList = milestones
				.stream()
				.filter(milestone -> milestone.getYearMonth().equals(yearMonth))
				.collect(Collectors.toList());
		return makeMessage(yearMonth, filteredList);
	}

	public String getMilestoneMessage(Integer year) {
		List<Milestone> filteredList = milestones
				.stream()
				.filter(milestone -> milestone.getYearMonth().getYear() == year)
				.collect(Collectors.toList());
		return makeMessage(YearMonth.of(year, 12), filteredList);
	}
	
	private String makeMessage(final YearMonth yearMonth, final List<Milestone> milestones) {
		StringBuffer sb = new StringBuffer("");
		if (milestones.size() > 0) {
			sb.append("<B>" + yearMonth.getYear() + "-" + yearMonth.getMonth().getValue() + "</B>");
			for (Milestone milestone : milestones) {
				sb.append("<P>" + messageSource.getMessage("msg.milestone." + milestone.getMilestoneType().getValue(), milestone.getMessageArgs(), LocaleContextHolder.getLocale()));
			}
		}		
		return sb.toString();
	}

	public Set<ScenarioYearBalance> getScenarioYearBalances() {
		Set<ScenarioYearBalance> ybs = new HashSet<ScenarioYearBalance>();
		for (Map.Entry<Integer, Integer> yearBalance : yearToLiquidBalanceMap.entrySet()) {
			ybs.add(new ScenarioYearBalance(yearBalance.getKey(), yearBalance.getValue()));
	    }
		return ybs;
	}
}
