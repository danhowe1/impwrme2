package com.impwrme2.model.cashflow;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public enum CashflowFrequency {

	MONTHLY (Values.MONTHLY),
	QUARTERLY (Values.QUARTERLY),
	ANNUALLY (Values.ANNUALLY),
	ONE_OFF (Values.ONE_OFF);

	private final String value;

	CashflowFrequency(String value) {
		this.value = value;
	}

    public static class Values {
        public static final String MONTHLY = "MONTHLY";
        public static final String QUARTERLY = "QUARTERLY";
        public static final String ANNUALLY = "ANNUALLY";
        public static final String ONE_OFF = "ONE_OFF";
    }

	public String getValue() {
		return value;
	}

	public String getValue(CashflowFrequency incomeExpenseFrequency) {
		return incomeExpenseFrequency.value;
	}

	public static CashflowFrequency getCashflowFrequency(String value) {
		return Stream.of(CashflowFrequency.values()).filter(c -> c.value.equals(value)).findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}

    public static CashflowFrequency getFrequency(String value) {
        if (value == null) {
            return null;
        }
 
        return Stream.of(CashflowFrequency.values())
          .filter(c -> c.value.equals(value))
          .findFirst()
          .orElseThrow(IllegalArgumentException::new);
    }

    public static List<String> listOfValues() {
    	return Arrays.asList(
    			CashflowFrequency.MONTHLY.value,
    			CashflowFrequency.QUARTERLY.value,
    			CashflowFrequency.ANNUALLY.value,
    			CashflowFrequency.ONE_OFF.value
    			);
    }
}
