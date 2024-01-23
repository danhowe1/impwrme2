package com.impwrme2.model.resource.enums;

import java.util.stream.Stream;

public enum ResourceParamNameEnum {

	SCENARIO_CPI(MessageCodes.SCENARIO_CPI),
	SCENARIO_SHARE_MARKET_GROWTH_RATE(MessageCodes.SCENARIO_SHARE_MARKET_GROWTH_RATE),
	PERSON_RETIREMENT_AGE(MessageCodes.PERSON_RETIREMENT_AGE);

	private final String messageCode;

	ResourceParamNameEnum(String messageCode) {
		this.messageCode = messageCode;
	}

	public static class MessageCodes {
		public static final String SCENARIO_CPI = "msg.class.resourceScenario.name.cpi";
		public static final String SCENARIO_SHARE_MARKET_GROWTH_RATE = "msg.class.resourceScenario.name.shareMarketGrowthRate";
		public static final String PERSON_RETIREMENT_AGE = "msg.class.resourcePerson.name.retirementAge";		
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
