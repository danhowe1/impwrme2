package com.impwrme2.model.cashflow;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(CashflowType.Values.INCOME)
public class CashflowIncome extends Cashflow {

	private static final long serialVersionUID = -2762991462764978544L;

	public static final String NAME_EMPLOYMENT_INCOME = "msg.class.cashflowIncome.name.employmentIncome";

	/**
	 * Protected constructor required for Hibernate only.
	 */
	protected CashflowIncome() {
		super();
	};

	public CashflowIncome(String name, CashflowFrequency frequency, Boolean cpiAffected) {
		super(name, "", frequency, cpiAffected);
	}
	
	public CashflowIncome(String name, String detail, CashflowFrequency frequency, Boolean cpiAffected) {
		super(name, detail, frequency, cpiAffected);
	}

	@Override
	public CashflowType getCashflowType() {
		return CashflowType.INCOME;
	}

}
