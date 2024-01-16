package com.impwrme2.model;

import java.time.YearMonth;

import jakarta.persistence.AttributeConverter;

public class YearMonthIntegerAttributeConverter implements AttributeConverter<YearMonth, Integer> {

	@Override
	public Integer convertToDatabaseColumn(YearMonth attribute) {
		if (null == attribute) {
			return null;
		}
		return (attribute.getYear() * 100 + attribute.getMonthValue());
	}

	@Override
	public YearMonth convertToEntityAttribute(Integer dbData) {
		if (null == dbData) {
			return null;
		}
		return YearMonth.of(dbData / 100, dbData % 100);
	}
}
