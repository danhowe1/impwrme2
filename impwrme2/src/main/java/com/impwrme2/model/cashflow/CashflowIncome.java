package com.impwrme2.model.cashflow;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(CashflowType.Values.INCOME)
public class CashflowIncome extends Cashflow {

	private static final long serialVersionUID = -2762991462764978544L;

	/**
	 * Protected constructor required for Hibernate only.
	 */
	protected CashflowIncome() {
		super();
	};

	public CashflowIncome(CashflowCategory category, CashflowFrequency frequency, Boolean cpiAffected) {
		super(category, "", frequency, cpiAffected);
	}
	
	public CashflowIncome(CashflowCategory category, String detail, CashflowFrequency frequency, Boolean cpiAffected) {
		super(category, detail, frequency, cpiAffected);
	}

	@Override
	public CashflowType getType() {
		return CashflowType.INCOME;
	}

}
