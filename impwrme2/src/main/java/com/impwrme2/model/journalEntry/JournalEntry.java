package com.impwrme2.model.journalEntry;

import java.io.Serializable;
import java.time.YearMonth;

import com.impwrme2.model.cashflow.CashflowCategory;
import com.impwrme2.model.resource.Resource;

public class JournalEntry implements Comparable<JournalEntry>, Serializable{

	private static final long serialVersionUID = -2429094711348472282L;

	public JournalEntry(Resource resource, YearMonth date, CashflowCategory category, Integer amount) {
		this(resource, date, category, null, amount);
	}
	
	public JournalEntry(Resource resource, YearMonth date, CashflowCategory category, String detail, Integer amount) {
		this.resource = resource;
		this.date = date;
		this.category = category;
		this.detail = detail;
		this.amount = amount;
	}
	
	private final Resource resource;
	
	private final YearMonth date;
	
	private final CashflowCategory category;
	
	private final String detail;
	
	private final Integer amount;
		
	public String toString() {
		StringBuffer sb = new StringBuffer();		
		sb.append(date.toString() + " ");
		sb.append(String.format("%1$-" + 40 + "s", category.getValue()));
		sb.append(String.format("%1$-" + 20 + "s", resource.getName()));
		sb.append(String.format("%1$-" + 20 + "s", null == detail ? "" : detail));
		sb.append(String.format("%1$" + 10 + "s", amount));
		return sb.toString();
	}
	
	//-------------------
	// Getters & setters.
	//-------------------
	
	public Resource getResource() {
		return resource;
	}

	public YearMonth getDate() {
		return date;
	}

	public CashflowCategory getCategory() {
		return category;
	}

	public String getDetail() {
		return detail;
	}

	public Integer getAmount() {
		return amount;
	}

	@Override
	public int compareTo(JournalEntry o) {
		if (this.getDate().isBefore(o.getDate())) {
			return -1;
		} else if (this.getDate().isAfter(o.getDate())) {
			return 1;
		}
		
		int journalTypeComparison = this.getCategory().compareTo(o.getCategory());
		if (journalTypeComparison != 0) {
			return this.getCategory().compareTo(o.getCategory());
		}
		
		int resourceComparison = this.getResource().compareTo(o.getResource());
		if (0 != resourceComparison) {
			return resourceComparison;
		}
		
		if (null != this.getDetail()) {
			int detailComparison = this.getDetail().compareTo(o.getDetail());
			if (0 != detailComparison) {
				return detailComparison;
			}
		}
		
		return 0;
	}
}
