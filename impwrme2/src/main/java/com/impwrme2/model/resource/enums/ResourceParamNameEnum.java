package com.impwrme2.model.resource.enums;

import java.util.stream.Stream;

public enum ResourceParamNameEnum {

	BALANCE_LIQUID_LEGAL_MAX(Values.BALANCE_LIQUID_LEGAL_MAX, "msg.class.resourceParamNameEnum.balanceLiquidLegalMax"),
	BALANCE_LIQUID_LEGAL_MIN(Values.BALANCE_LIQUID_LEGAL_MIN, "msg.class.resourceParamNameEnum.balanceLiquidLegalMin"),
	BALANCE_LIQUID_PREFERRED_MIN(Values.BALANCE_LIQUID_PREFERRED_MIN, "msg.class.resourceParamNameEnum.balanceLiquidPreferredMin"),
	BALANCE_OPENING_LIQUID(Values.BALANCE_OPENING_LIQUID, "msg.class.resourceParamNameEnum.balanceOpeningLiquid"),
	BALANCE_OPENING_FIXED(Values.BALANCE_OPENING_FIXED, "msg.class.resourceParamNameEnum.balanceOpeningFixed"),
	
	SCENARIO_CPI(Values.SCENARIO_CPI, "msg.class.resourceParamNameEnum.cpi"),
	SCENARIO_SHARE_MARKET_GROWTH_RATE(Values.SCENARIO_SHARE_MARKET_GROWTH_RATE, "msg.class.resourceParamNameEnum.shareMarketGrowthRate"),
	PERSON_BIRTH_YEAR_MONTH(Values.PERSON_BIRTH_YEAR_MONTH, "msg.class.resourceParamNameEnum.birthMonthYear"),
	PERSON_DEPARTURE_AGE(Values.PERSON_DEPARTURE_AGE, "msg.class.resourceParamNameEnum.departureAge"),
	PERSON_RETIREMENT_AGE(Values.PERSON_RETIREMENT_AGE, "msg.class.resourceParamNameEnum.retirementAge");

	private final String value;
	private final String messageCode;

	ResourceParamNameEnum(String value, String messageCode) {
		this.value = value;
		this.messageCode = messageCode;
	}

	public static class Values {
		public static final String BALANCE_LIQUID_LEGAL_MAX = "BALANCE_LIQUID_LEGAL_MAX";
		public static final String BALANCE_LIQUID_LEGAL_MIN = "BALANCE_LIQUID_LEGAL_MIN";
		public static final String BALANCE_LIQUID_PREFERRED_MIN = "BALANCE_LIQUID_PREFERRED_MIN";
		public static final String BALANCE_OPENING_LIQUID = "BALANCE_OPENING_LIQUID";
		public static final String BALANCE_OPENING_FIXED = "BALANCE_OPENING_FIXED";
		
		public static final String SCENARIO_CPI = "SCENARIO_CPI";
		public static final String SCENARIO_SHARE_MARKET_GROWTH_RATE = "SCENARIO_SHARE_MARKET_GROWTH_RATE";
		public static final String PERSON_BIRTH_YEAR_MONTH = "PERSON_BIRTH_YEAR_MONTH";		
		public static final String PERSON_DEPARTURE_AGE = "PERSON_DEPARTURE_AGE";		
		public static final String PERSON_RETIREMENT_AGE = "PERSON_RETIREMENT_AGE";		
	}

	public String getValue() {
		return value;
	}

	public String getValue(ResourceParamNameEnum paramName) {
		return paramName.value;
	}

	public String getMessageCode(ResourceParamNameEnum paramName) {
		return 	paramName.getMessageCode();
	}

	public String getMessageCode() {
		return messageCode;
	}
	public static ResourceParamNameEnum getParamNameFromValue(String value) {
		return Stream.of(ResourceParamNameEnum.values()).filter(c -> c.value.equals(value)).findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}

	public static ResourceParamNameEnum getParamNameFromMessageCode(String messageCode) {
		return Stream.of(ResourceParamNameEnum.values()).filter(c -> c.messageCode.equals(messageCode)).findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}
}
