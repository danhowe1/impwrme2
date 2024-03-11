package com.impwrme2.model.resource.enums;

import java.util.stream.Stream;

public enum ResourceParamNameEnum {

	BALANCE_LIQUID_LEGAL_MAX(MessageCodes.BALANCE_LIQUID_LEGAL_MAX),
	BALANCE_LIQUID_LEGAL_MIN(MessageCodes.BALANCE_LIQUID_LEGAL_MIN),
	BALANCE_OPENING_LIQUID(MessageCodes.BALANCE_OPENING_LIQUID),
	BALANCE_OPENING_FIXED(MessageCodes.BALANCE_OPENING_FIXED),
	
	SCENARIO_CPI(MessageCodes.SCENARIO_CPI),
	SCENARIO_SHARE_MARKET_GROWTH_RATE(MessageCodes.SCENARIO_SHARE_MARKET_GROWTH_RATE),
	PERSON_BIRTH_YEAR_MONTH(MessageCodes.PERSON_BIRTH_YEAR_MONTH),
	PERSON_DEPARTURE_AGE(MessageCodes.PERSON_DEPARTURE_AGE),
	PERSON_RETIREMENT_AGE(MessageCodes.PERSON_RETIREMENT_AGE);

	private final String messageCode;

	ResourceParamNameEnum(String messageCode) {
		this.messageCode = messageCode;
	}

	public static class MessageCodes {
		public static final String BALANCE_LIQUID_LEGAL_MAX = "msg.class.resourceParamNameEnum.balanceLiquidLegalMax";
		public static final String BALANCE_LIQUID_LEGAL_MIN = "msg.class.resourceParamNameEnum.balanceLiquidLegalMin";
		public static final String BALANCE_OPENING_LIQUID = "msg.class.resourceParamNameEnum.balanceOpeningLiquid";
		public static final String BALANCE_OPENING_FIXED = "msg.class.resourceParamNameEnum.balanceOpeningFixed";
		
		public static final String SCENARIO_CPI = "msg.class.resourceParamNameEnum.cpi";
		public static final String SCENARIO_SHARE_MARKET_GROWTH_RATE = "msg.class.resourceParamNameEnum.shareMarketGrowthRate";
		public static final String PERSON_BIRTH_YEAR_MONTH = "msg.class.resourceParamNameEnum.birthMonthYear";		
		public static final String PERSON_DEPARTURE_AGE = "msg.class.resourceParamNameEnum.departureAge";		
		public static final String PERSON_RETIREMENT_AGE = "msg.class.resourceParamNameEnum.retirementAge";		
	}

	public String getMessageCode(ResourceParamNameEnum paramName) {
		return 	paramName.getMessageCode();
	}

	public String getMessageCode() {
		return messageCode;
	}

	public static ResourceParamNameEnum getParamName(String messageCode) {
		return Stream.of(ResourceParamNameEnum.values()).filter(c -> c.messageCode.equals(messageCode)).findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}
}
