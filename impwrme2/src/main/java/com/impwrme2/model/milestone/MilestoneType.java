package com.impwrme2.model.milestone;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;

public enum MilestoneType {

	MORTGAGE_PAYOUT_DATE(Values.MORTGAGE_PAYOUT_DATE),
	PERSON_DEPARTURE_AGE(Values.PERSON_DEPARTURE_AGE),
	PERSON_RETIREMENT_AGE(Values.PERSON_RETIREMENT_AGE),
	PROPERTY_SALE(Values.PROPERTY_SALE),
	SUPER_PRESERVATION_AGE(Values.SUPER_PRESERVATION_AGE);

	public static final List<MilestoneType> MILESTONE_TYPES = new ArrayList<MilestoneType>(EnumSet.allOf(MilestoneType.class));

	private final String value;

	MilestoneType(String value) {
		this.value = value;
	}

	public static class Values {
		public static final String MORTGAGE_PAYOUT_DATE = "MORTGAGE_PAYOUT_DATE";
		public static final String PERSON_DEPARTURE_AGE = "PERSON_DEPARTURE_AGE";
		public static final String PERSON_RETIREMENT_AGE = "PERSON_RETIREMENT_AGE";
		public static final String PROPERTY_SALE = "PROPERTY_SALE";
		public static final String SUPER_PRESERVATION_AGE = "SUPER_PRESERVATION_AGE";
	}

	public String getValue() {
		return value;
	}

	public String getValue(MilestoneType milestoneType) {
		return milestoneType.value;
	}

	public static MilestoneType getMilestoneType(String value) {
		return Stream.of(MilestoneType.values()).filter(c -> c.value.equals(value)).findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}
}
