package com.impwrme2.model.resourceParam;

import java.util.stream.Stream;

public enum ResourceParamType {

	BIG_DECIMAL(Values.BIG_DECIMAL),
//	BOOLEAN_TRUE(Values.BOOLEAN_TRUE),
	INTEGER(Values.INTEGER),
	STRING(Values.STRING),
	YEAR_MONTH(Values.YEAR_MONTH);
	
	private final String value;

	ResourceParamType(String value) {
		this.value = value;
	}

	public static class Values {
		public static final String BIG_DECIMAL = "BIG_DECIMAL";
//		public static final String BOOLEAN_TRUE = "BOOLEAN_TRUE";
		public static final String INTEGER = "INTEGER";
		public static final String STRING = "STRING";
		public static final String YEAR_MONTH = "YEAR_MONTH";
	}

	public String getValue() {
		return value;
	}

	public String getValue(ResourceParamType resourceParamType) {
		return resourceParamType.value;
	}

	public static ResourceParamType getResourceParamType(String value) {
		return Stream.of(ResourceParamType.values()).filter(c -> c.value.equals(value)).findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}
}
