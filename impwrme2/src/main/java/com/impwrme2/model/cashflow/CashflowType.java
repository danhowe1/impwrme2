package com.impwrme2.model.cashflow;

import java.util.stream.Stream;

public enum CashflowType {

//	APPRECIATION(Values.APPRECIATION),
	DEPOSIT(Values.DEPOSIT),
	WITHDRAWAL(Values.WITHDRAWAL),
	EXPENSE(Values.EXPENSE),
	INCOME(Values.INCOME);
	
	private final String value;

	CashflowType(String value) {
		this.value = value;
	}

	public static class Values {
//		public static final String APPRECIATION = "APPRECIATION";
		public static final String DEPOSIT = "DEPOSIT";
		public static final String WITHDRAWAL = "WITHDRAWAL";
		public static final String EXPENSE = "EXPENSE";
		public static final String INCOME = "INCOME";
	}

	public String getValue() {
		return value;
	}

	public String getValue(CashflowType resourceParamType) {
		return resourceParamType.value;
	}

	public static CashflowType getCashflowType(String value) {
		return Stream.of(CashflowType.values()).filter(c -> c.value.equals(value)).findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}
}
