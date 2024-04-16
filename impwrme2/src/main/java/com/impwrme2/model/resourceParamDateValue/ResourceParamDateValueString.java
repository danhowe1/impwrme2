package com.impwrme2.model.resourceParamDateValue;

import java.time.YearMonth;

import com.impwrme2.model.resourceParam.ResourceParamType;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;

@Entity
@DiscriminatorValue(ResourceParamType.Values.STRING)
public class ResourceParamDateValueString extends ResourceParamDateValue<String> {

	private static final long serialVersionUID = 2526957249981540013L;

	protected ResourceParamDateValueString() {
		super();
	}
	
	public ResourceParamDateValueString(YearMonth yearMonth, boolean userAbleToChangeDate, String value) {
		super(yearMonth, userAbleToChangeDate, value);
	}

	private String value;
	
	@Override
	@NotNull(message = "{msg.validation.resourceParamDateValue.value.notEmpty}")
	public String getValue() {
		return value;
	}

	@Override
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public void setValueFromString(String value) {
		this.value = value;
	}
}
