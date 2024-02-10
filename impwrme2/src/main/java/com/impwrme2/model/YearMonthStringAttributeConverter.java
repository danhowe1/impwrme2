package com.impwrme2.model;

import java.time.YearMonth;

import jakarta.persistence.AttributeConverter;

public class YearMonthStringAttributeConverter implements AttributeConverter<YearMonth, String> {

	@Override
	public String convertToDatabaseColumn(YearMonth attribute) {
		if (null == attribute) {
			return null;
		}		
		return String.valueOf(attribute.getYear()) + "%02d".formatted(attribute.getMonthValue());
	}

	@Override
	public YearMonth convertToEntityAttribute(String dbData) {
		if (null == dbData) {
			return null;
		}
		Integer year = Integer.valueOf(dbData.substring(0, 4));
		Integer month = Integer.valueOf(dbData.substring(4));
		return YearMonth.of(year, month);
	}
}
