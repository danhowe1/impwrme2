package com.impwrme2.model.journalEntry;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class JournalEntryResponse implements Serializable {

	private static final long serialVersionUID = -8364314557845566906L;
	
	private List<JournalEntry> journalEntries;

	public List<JournalEntry> getJournalEntries() {
		return journalEntries;
	}

	public void setJournalEntries(List<JournalEntry> journalEntries) {
		this.journalEntries = journalEntries;
	}
}
