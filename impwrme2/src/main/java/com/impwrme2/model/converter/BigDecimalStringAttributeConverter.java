package com.impwrme2.model.converter;

import java.math.BigDecimal;

import jakarta.persistence.AttributeConverter;

public class BigDecimalStringAttributeConverter implements AttributeConverter<BigDecimal, String> {

	@Override
	public String convertToDatabaseColumn(BigDecimal attribute) {
		if (null == attribute) return null;
		return String.valueOf(attribute);
	}

	@Override
	public BigDecimal convertToEntityAttribute(String dbData) {
		return new BigDecimal(dbData);
	}
}
