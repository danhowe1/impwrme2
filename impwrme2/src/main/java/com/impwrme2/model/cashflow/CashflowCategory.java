package com.impwrme2.model.cashflow;

import java.util.stream.Stream;

public enum CashflowCategory {
	
	ADDITIONAL_PAYMENT(Values.ADDITIONAL_PAYMENT),
	ASSET_OWNERSHIP(Values.ASSET_OWNERSHIP),
	ASSET_SALE(Values.ASSET_SALE),
	BALANCE(Values.BALANCE),
	DIVIDENDS(Values.DIVIDENDS),
	EMPLOYMENT(Values.EMPLOYMENT),
	FEES(Values.FEES),
	GROWTH(Values.GROWTH),
	HOLIDAYS(Values.HOLIDAYS),
	INTEREST(Values.INTEREST),
	LIVING_ESSENTIAL(Values.LIVING_ESSENTIAL),
	LIVING_NON_ESSENTIAL(Values.LIVING_NON_ESSENTIAL),
	PRINCIPAL(Values.PRINCIPAL),
	REDRAW(Values.REDRAW),
	RENT(Values.RENT),
	UTILITIES(Values.UTILITIES);

	private final String value;

	CashflowCategory(String value) {
		this.value = value;
	}

	public static class Values {
		public static final String ADDITIONAL_PAYMENT = "ADDITIONAL_PAYMENT";
		public static final String ASSET_OWNERSHIP = "ASSET_OWNERSHIP";
		public static final String ASSET_SALE = "ASSET_SALE";
		public static final String BALANCE = "BALANCE";
		public static final String DIVIDENDS = "DIVIDENDS";
		public static final String EMPLOYMENT = "EMPLOYMENT";
		public static final String FEES = "FEES";
		public static final String GROWTH = "GROWTH";
		public static final String HOLIDAYS = "HOLIDAYS";
		public static final String INTEREST = "INTEREST";
		public static final String LIVING_ESSENTIAL = "LIVING_ESSENTIAL";
		public static final String LIVING_NON_ESSENTIAL = "LIVING_NON_ESSENTIAL";
		public static final String PRINCIPAL = "PRINCIPAL";
		public static final String REDRAW = "REDRAW";
		public static final String RENT = "RENT";
		public static final String UTILITIES = "UTILITIES";
	}

	public String getValue() {
		return value;
	}

	public String getValue(CashflowCategory category) {
		return category.value;
	}

	public static CashflowCategory getCategory(String value) {
		return Stream.of(CashflowCategory.values()).filter(c -> c.value.equals(value)).findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}
}
