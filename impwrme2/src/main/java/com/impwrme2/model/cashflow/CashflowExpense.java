package com.impwrme2.model.cashflow;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(CashflowType.Values.EXPENSE)
public class CashflowExpense extends Cashflow {

	private static final long serialVersionUID = -4545932392540030968L;

	/**
	 * Protected constructor required for Hibernate only.
	 */
	protected CashflowExpense() {
		super();
	};

	public CashflowExpense(CashflowCategory category, CashflowFrequency frequency, Boolean cpiAffected) {
		super(category, "", frequency, cpiAffected);
	}
	
	public CashflowExpense(CashflowCategory category, String detail, CashflowFrequency frequency, Boolean cpiAffected) {
		super(category, detail, frequency, cpiAffected);
	}

	@Override
	public CashflowType getType() {
		return CashflowType.EXPENSE;
	}

}
