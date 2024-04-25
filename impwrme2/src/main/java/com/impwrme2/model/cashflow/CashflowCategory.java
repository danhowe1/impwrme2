package com.impwrme2.model.cashflow;

import java.util.stream.Stream;

public enum CashflowCategory {
	
	JE_BALANCE_OPENING_LIQUID(Values.JE_BALANCE_OPENING_LIQUID, CashflowType.JE_BALANCE_OPENING),
	JE_BALANCE_OPENING_ASSET_VALUE(Values.JE_BALANCE_OPENING_ASSET_VALUE, CashflowType.JE_BALANCE_OPENING),
	
	EXPENSE_LIVING_ESSENTIAL(Values.EXPENSE_LIVING_ESSENTIAL, CashflowType.EXPENSE),
	EXPENSE_LIVING_NON_ESSENTIAL(Values.EXPENSE_LIVING_NON_ESSENTIAL, CashflowType.EXPENSE),
//	EXPENSE_UTILITIES(Values.EXPENSE_UTILITIES, CashflowType.EXPENSE),
	EXPENSE_RENT(Values.EXPENSE_RENT, CashflowType.EXPENSE),
	EXPENSE_HOLIDAYS(Values.EXPENSE_HOLIDAYS, CashflowType.EXPENSE),
	EXPENSE_ASSET_PURCHASE_DEPOSIT(Values.EXPENSE_ASSET_PURCHASE_DEPOSIT, CashflowType.EXPENSE),
	EXPENSE_ASSET_PURCHASE_ADDITIONAL_COSTS(Values.EXPENSE_ASSET_PURCHASE_ADDITIONAL_COSTS, CashflowType.EXPENSE),
	EXPENSE_ASSET_OWNERSHIP(Values.EXPENSE_ASSET_OWNERSHIP, CashflowType.EXPENSE),
	EXPENSE_INTEREST(Values.EXPENSE_INTEREST, CashflowType.EXPENSE),
	EXPENSE_PRINCIPAL(Values.EXPENSE_PRINCIPAL, CashflowType.EXPENSE),
	EXPENSE_RENTAL_PROPERTY(Values.EXPENSE_RENTAL_PROPERTY, CashflowType.EXPENSE),
	EXPENSE_ASSET_SALE_COMMISSION(Values.EXPENSE_ASSET_SALE_COMMISSION, CashflowType.EXPENSE),
	EXPENSE_ASSET_SALE_FIXED(Values.EXPENSE_ASSET_SALE_FIXED, CashflowType.EXPENSE),
	EXPENSE_FEES(Values.EXPENSE_FEES, CashflowType.EXPENSE),
	EXPENSE_MISC(Values.EXPENSE_MISC, CashflowType.EXPENSE),	
	
	INCOME_EMPLOYMENT(Values.INCOME_EMPLOYMENT, CashflowType.INCOME),
	INCOME_RENTAL_PROPERTY(Values.INCOME_RENTAL_PROPERTY, CashflowType.INCOME),
	INCOME_DIVIDENDS(Values.INCOME_DIVIDENDS, CashflowType.INCOME),
	INCOME_ASSET_SALE(Values.INCOME_ASSET_SALE, CashflowType.INCOME),
	INCOME_MISC(Values.INCOME_MISC, CashflowType.INCOME),
	
	WITHDRAWAL_BALANCE(Values.WITHDRAWAL_BALANCE, CashflowType.WITHDRAWAL),
	WITHDRAWAL_REDRAW(Values.WITHDRAWAL_REDRAW, CashflowType.WITHDRAWAL),
	JE_USER_WITHDRAWAL(Values.JE_USER_WITHDRAWAL, CashflowType.WITHDRAWAL),
	JE_AUTO_WITHDRAWAL(Values.JE_AUTO_WITHDRAWAL, CashflowType.WITHDRAWAL),
		
	DEPOSIT_BALANCE(Values.DEPOSIT_BALANCE, CashflowType.DEPOSIT),
	DEPOSIT_ADDITIONAL_PAYMENT(Values.DEPOSIT_ADDITIONAL_PAYMENT, CashflowType.DEPOSIT),
	JE_AUTO_DEPOSIT(Values.JE_AUTO_DEPOSIT, CashflowType.DEPOSIT),
	JE_USER_DEPOSIT(Values.JE_USER_DEPOSIT, CashflowType.DEPOSIT),

	APPRECIATION_ASSET_SALE(Values.APPRECIATION_ASSET_SALE, CashflowType.APPRECIATION),
	APPRECIATION_GROWTH(Values.APPRECIATION_GROWTH, CashflowType.APPRECIATION),

	DEPRECIATION_SALE(Values.DEPRECIATION_SALE, CashflowType.DEPRECIATION),
	
	JE_BALANCE_CLOSING_LIQUID(Values.JE_BALANCE_CLOSING_LIQUID, CashflowType.JE_BALANCE_CLOSING),
	JE_BALANCE_CLOSING_ASSET_VALUE(Values.JE_BALANCE_CLOSING_ASSET_VALUE, CashflowType.JE_BALANCE_CLOSING);

	//	JE_BALANCE_CLOSING_LIQUID_AVAILABLE_TO_POT(Values.JE_BALANCE_CLOSING_LIQUID_AVAILABLE_TO_POT, CashflowType.JE_BALANCE_CLOSING),
//	JE_BALANCE_CLOSING_LIQUID_UNAVAILABLE_TO_POT(Values.JE_BALANCE_CLOSING_LIQUID_UNAVAILABLE_TO_POT, CashflowType.JE_BALANCE_CLOSING),
//	JE_BALANCE_CLOSING_ASSET_VALUE_UNAVAILABLE_TO_POT(Values.JE_BALANCE_CLOSING_ASSET_VALUE_UNAVAILABLE_TO_POT, CashflowType.JE_BALANCE_CLOSING);

	private final String value;
	private final CashflowType type;

	CashflowCategory(String value, CashflowType type) {
		this.value = value;
		this.type = type;
	}

	public static class Values {
		public static final String JE_BALANCE_OPENING_LIQUID = "JE_BALANCE_OPENING_LIQUID";
		public static final String JE_BALANCE_OPENING_ASSET_VALUE = "JE_BALANCE_OPENING_ASSET_VALUE";
		public static final String JE_AUTO_DEPOSIT = "JE_AUTO_DEPOSIT";
		public static final String JE_AUTO_WITHDRAWAL = "JE_AUTO_WITHDRAWAL";
		public static final String JE_USER_DEPOSIT = "JE_USER_DEPOSIT";
		public static final String JE_USER_WITHDRAWAL = "JE_USER_WITHDRAWAL";
		public static final String APPRECIATION_ASSET_SALE = "APPRECIATION_ASSET_SALE";
		public static final String APPRECIATION_GROWTH = "APPRECIATION_GROWTH";
		public static final String DEPOSIT_ADDITIONAL_PAYMENT = "DEPOSIT_ADDITIONAL_PAYMENT";
		public static final String DEPOSIT_BALANCE = "DEPOSIT_BALANCE";
		public static final String DEPRECIATION_SALE = "DEPRECIATION_SALE";
		public static final String EXPENSE_ASSET_OWNERSHIP = "EXPENSE_ASSET_OWNERSHIP";
		public static final String EXPENSE_ASSET_PURCHASE_ADDITIONAL_COSTS = "EXPENSE_ASSET_PURCHASE_ADDITIONAL_COSTS";
		public static final String EXPENSE_ASSET_PURCHASE_DEPOSIT = "EXPENSE_ASSET_PURCHASE_DEPOSIT";
		public static final String EXPENSE_ASSET_SALE_COMMISSION = "EXPENSE_ASSET_SALE_COMMISSION";
		public static final String EXPENSE_ASSET_SALE_FIXED = "EXPENSE_ASSET_SALE_FIXED";
		public static final String EXPENSE_FEES = "EXPENSE_FEES";
		public static final String EXPENSE_HOLIDAYS = "EXPENSE_HOLIDAYS";
		public static final String EXPENSE_INTEREST = "EXPENSE_INTEREST";
		public static final String EXPENSE_LIVING_ESSENTIAL = "EXPENSE_LIVING_ESSENTIAL";
		public static final String EXPENSE_LIVING_NON_ESSENTIAL = "EXPENSE_LIVING_NON_ESSENTIAL";
		public static final String EXPENSE_MISC = "EXPENSE_MISC";
		public static final String EXPENSE_PRINCIPAL = "EXPENSE_PRINCIPAL";
		public static final String EXPENSE_RENT = "EXPENSE_RENT";
		public static final String EXPENSE_RENTAL_PROPERTY = "EXPENSE_RENTAL_PROPERTY";
//		public static final String EXPENSE_UTILITIES = "EXPENSE_UTILITIES";
		public static final String INCOME_ASSET_SALE = "INCOME_ASSET_SALE";
		public static final String INCOME_DIVIDENDS = "INCOME_DIVIDENDS";
		public static final String INCOME_EMPLOYMENT = "INCOME_EMPLOYMENT";
		public static final String INCOME_MISC = "INCOME_MISC";
		public static final String INCOME_RENTAL_PROPERTY = "INCOME_RENTAL_PROPERTY";
		public static final String WITHDRAWAL_BALANCE = "WITHDRAWAL_BALANCE";
		public static final String WITHDRAWAL_REDRAW = "WITHDRAWAL_REDRAW";
		public static final String JE_BALANCE_CLOSING_LIQUID = "JE_BALANCE_CLOSING_LIQUID";
		public static final String JE_BALANCE_CLOSING_ASSET_VALUE = "JE_BALANCE_CLOSING_ASSET_VALUE";
//		public static final String JE_BALANCE_CLOSING_LIQUID_AVAILABLE_TO_POT = "JE_BALANCE_CLOSING_LIQUID_AVAILABLE_TO_POT";
//		public static final String JE_BALANCE_CLOSING_LIQUID_UNAVAILABLE_TO_POT = "JE_BALANCE_CLOSING_LIQUID_UNAVAILABLE_TO_POT";
//		public static final String JE_BALANCE_CLOSING_ASSET_VALUE_UNAVAILABLE_TO_POT= "JE_BALANCE_CLOSING_ASSET_VALUE_UNAVAILABLE_TO_POT";
	}

	public String getValue() {
		return value;
	}

	public String getValue(CashflowCategory category) {
		return category.value;
	}

	public CashflowType getType() {
		return type;
	}

	public String getMessageCode() {
		return "msg.class.cashflowCategory." + CashflowCategory.getCategory(value).getValue();
	}
	
	public static CashflowCategory getCategory(String value) {
		return Stream.of(CashflowCategory.values()).filter(c -> c.value.equals(value)).findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}
}
