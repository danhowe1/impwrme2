package com.impwrme2.controller.dto;

import com.impwrme2.model.resourceParam.ResourceParamType;

public class DtoUtils {

	public static String resourceParamValueRegex(ResourceParamType resourceParamType) {
		switch (resourceParamType) {
		case BIG_DECIMAL:
			return "^\\s*-?(\\d+(\\.\\d{1,2})?|\\.\\d{1,2})\\s*$";
		case INTEGER_NEGATIVE:
			return "^-?\\d+$";
		case INTEGER_POSITIVE:
			return "^\\d+$";
		case STRING:
			return "^.+";
		case YEAR_MONTH:
			return "^[0-9]{1,2}.[0-9]{4}";
		default:
			throw new IllegalStateException("Unknown resource param type " + resourceParamType);
		}
	}
}
