package com.impwrme2.model.cashflow;

import java.util.stream.Stream;

public enum CashflowType {

	JE_BALANCE_OPENING(Values.JE_BALANCE_OPENING),
	DEPOSIT(Values.DEPOSIT),
	WITHDRAWAL(Values.WITHDRAWAL),
	EXPENSE(Values.EXPENSE),
	INCOME(Values.INCOME),
	JE_BALANCE_CLOSING(Values.JE_BALANCE_CLOSING);
	
	private final String value;

	CashflowType(String value) {
		this.value = value;
	}

	public static class Values {
		public static final String JE_BALANCE_OPENING = "JE_BALANCE_OPENING";
		public static final String DEPOSIT = "DEPOSIT";
		public static final String WITHDRAWAL = "WITHDRAWAL";
		public static final String EXPENSE = "EXPENSE";
		public static final String INCOME = "INCOME";
		public static final String JE_BALANCE_CLOSING = "JE_BALANCE_CLOSING";
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
