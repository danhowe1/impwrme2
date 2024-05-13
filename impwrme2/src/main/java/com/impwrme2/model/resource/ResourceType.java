package com.impwrme2.model.resource;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;

public enum ResourceType {

	CREDIT_CARD(Values.CREDIT_CARD),
	MORTGAGE_OFFSET_ACCOUNT(Values.MORTGAGE_OFFSET_ACCOUNT),
	SAVINGS_ACCOUNT(Values.SAVINGS_ACCOUNT),
	SUPERANNUATION(Values.SUPERANNUATION),
	SHARES(Values.SHARES),
	CURRENT_ACCOUNT(Values.CURRENT_ACCOUNT),
	MORTGAGE(Values.MORTGAGE),
	UNALLOCATED(Values.UNALLOCATED),

	SCENARIO(Values.SCENARIO),
	HOUSEHOLD(Values.HOUSEHOLD),
	PERSON(Values.PERSON),
	PROPERTY_EXISTING(Values.PROPERTY_EXISTING),
	PROPERTY_NEW(Values.PROPERTY_NEW);

	public static final List<ResourceType> RESOURCE_TYPES = new ArrayList<ResourceType>(EnumSet.allOf(ResourceType.class));

	private final String value;

	ResourceType(String value) {
		this.value = value;
	}

	public static class Values {
		public static final String SCENARIO = "SCENARIO";
		public static final String CREDIT_CARD = "CREDIT_CARD";
		public static final String CURRENT_ACCOUNT = "CURRENT_ACCOUNT";
		public static final String HOUSEHOLD = "HOUSEHOLD";
		public static final String MORTGAGE = "MORTGAGE";
		public static final String MORTGAGE_OFFSET_ACCOUNT = "MORTGAGE_OFFSET_ACCOUNT";
		public static final String PERSON = "PERSON";
		public static final String PROPERTY_EXISTING = "PROPERTY_EXISTING";
		public static final String PROPERTY_NEW = "PROPERTY_NEW";
		public static final String SAVINGS_ACCOUNT = "SAVINGS_ACCOUNT";
		public static final String SHARES = "SHARES";
		public static final String SUPERANNUATION = "SUPERANNUATION";
		public static final String UNALLOCATED = "UNALLOCATED";
	}

	public String getValue() {
		return value;
	}

	public String getValue(ResourceType resourceType) {
		return resourceType.value;
	}

	public static ResourceType getResourceType(String value) {
		return Stream.of(ResourceType.values()).filter(c -> c.value.equals(value)).findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}
}
