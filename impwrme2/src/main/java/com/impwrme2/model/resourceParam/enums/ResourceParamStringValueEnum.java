package com.impwrme2.model.resourceParam.enums;

import java.util.stream.Stream;

public enum ResourceParamStringValueEnum {

	PROPERTY_STATUS_LIVING_IN(Values.PROPERTY_STATUS_LIVING_IN, "msg.class.resourceParamStringValueEnum.PROPERTY_STATUS_LIVING_IN"),
	PROPERTY_STATUS_RENTED(Values.PROPERTY_STATUS_RENTED, "msg.class.resourceParamStringValueEnum.PROPERTY_STATUS_RENTED"),
	PROPERTY_STATUS_SOLD(Values.PROPERTY_STATUS_SOLD, "msg.class.resourceParamStringValueEnum.PROPERTY_STATUS_SOLD");
	
	private final String value;
	private final String messageCode;

	ResourceParamStringValueEnum(final String value, final String messageCode) {
		this.value = value;
		this.messageCode = messageCode;
	}

	public String getValue() {
		return value;
	}
	
	public String getMessageCode() {
		return messageCode;
	}
	
	public static String getMessageCode(String value) {
		return Stream.of(ResourceParamStringValueEnum.values()).filter(c -> c.value.equals(value)).findFirst().get().getMessageCode();
	}
	
	public static class Values {
		public static final String PROPERTY_STATUS_LIVING_IN = "PROPERTY_STATUS_LIVING_IN";
		public static final String PROPERTY_STATUS_RENTED = "PROPERTY_STATUS_RENTED";
		public static final String PROPERTY_STATUS_SOLD = "PROPERTY_STATUS_SOLD";
	}
}
