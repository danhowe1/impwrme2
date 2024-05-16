package com.impwrme2.model.journalEntry;

import java.io.Serializable;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import com.impwrme2.model.milestone.Milestone;

@Component
public class JournalEntryResponse implements Serializable {

	private static final long serialVersionUID = -8364314557845566906L;
	
	@Autowired
	MessageSource messageSource;
	
	private List<JournalEntry> journalEntries;
	
	private List<Milestone> milestones = new ArrayList<Milestone>();

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
	
//	public String getMilestoneMessage(String resourceName, YearMonth yearMonth) {
//		List<Milestone> filteredList = milestones
//				.stream()
//				.filter(milestone -> milestone.getResource().getName().equals(resourceName) && milestone.getYearMonth().equals(yearMonth))
//				.collect(Collectors.toList());
//		return makeMessage(filteredList);
//	}
//
//	public String getMilestoneMessage(String resourceName, Integer year) {
//		List<Milestone> filteredList = milestones
//				.stream()
//				.filter(milestone -> milestone.getResource().getName().equals(resourceName) && milestone.getYearMonth().getYear() == year)
//				.collect(Collectors.toList());
//		return makeMessage(filteredList);
//	}
	
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
			sb.append(yearMonth.getMonth().getValue() + "-" + yearMonth.getYear() + " : ");
			for (Milestone milestone : milestones) {
				sb.append(messageSource.getMessage("msg.milestone." + milestone.getMilestoneType().getValue(), milestone.getMessageArgs(), LocaleContextHolder.getLocale()));
			}
		}
		return sb.toString();
	}
}
