package com.impwrme2.model.resourceParamDateValue;

import java.time.YearMonth;

import com.impwrme2.model.converter.IntegerStringAttributeConverter;
import com.impwrme2.model.resourceParam.ResourceParamType;

import jakarta.persistence.Convert;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;

@Entity
@DiscriminatorValue(ResourceParamType.Values.INTEGER_POSITIVE)
public class ResourceParamDateValueIntegerPositive extends ResourceParamDateValue<Integer> {

	private static final long serialVersionUID = 5480752038541088828L;

	protected ResourceParamDateValueIntegerPositive() {
		super();
	}
	
	public ResourceParamDateValueIntegerPositive(YearMonth yearMonth, boolean userAbleToChangeDate, final Integer value) {
		super(yearMonth, userAbleToChangeDate, value);
	}

	public ResourceParamDateValueIntegerPositive(YearMonth yearMonth, boolean userAbleToChangeDate, final String value) {
		super(yearMonth, userAbleToChangeDate, value);
	}

	@Convert(converter = IntegerStringAttributeConverter.class)
	private Integer value;

	@Override
	@NotNull(message = "{msg.validation.resourceParamDateValue.value.notEmpty}")
	public Integer getValue() {
		return this.value;
	}

	@Override
	public void setValue(Integer value) {
		if (value < 0) {
			this.value = -value;			
		} else {
			this.value = value;					
		}
	}

	@Override
	public void setValueFromString(String value) {
		setValue(Integer.valueOf(value));		
	}
}
