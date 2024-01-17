package com.impwrme2.model.converter;

import jakarta.persistence.AttributeConverter;

public class IntegerStringAttributeConverter implements AttributeConverter<Integer, String> {

	@Override
	public String convertToDatabaseColumn(Integer attribute) {
		if (null == attribute) return null;
		return String.valueOf(attribute);
	}

	@Override
	public Integer convertToEntityAttribute(String dbData) {
		return Integer.valueOf(dbData);
	}
}
