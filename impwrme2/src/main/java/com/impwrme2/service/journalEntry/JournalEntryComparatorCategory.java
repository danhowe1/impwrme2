package com.impwrme2.service.journalEntry;

import java.util.Comparator;

import com.impwrme2.model.journalEntry.JournalEntry;

public class JournalEntryComparatorCategory implements Comparator<JournalEntry> {

	@Override
	public int compare(JournalEntry je1, JournalEntry je2) {
		
		int categoryCompare = je1.getCategory().compareTo(je2.getCategory());
		if (categoryCompare != 0) {
			return categoryCompare;
		}

		return je1.getResource().getName().compareTo(je2.getResource().getName());
	}
}
