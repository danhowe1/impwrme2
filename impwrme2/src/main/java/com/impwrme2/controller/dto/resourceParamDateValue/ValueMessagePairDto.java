package com.impwrme2.controller.dto.resourceParamDateValue;

public class ValueMessagePairDto {

	private final String value;
	private final String message;

	public ValueMessagePairDto(final String value, final String message) {
		this.value = value;
		this.message = message;
	}

	public String getValue() {
		return value;
	}

	public String getMessage() {
		return message;
	}
}
