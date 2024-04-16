package com.impwrme2.model.resourceParamDateValue;

import java.math.BigDecimal;
import java.time.YearMonth;

import com.impwrme2.model.converter.BigDecimalStringAttributeConverter;
import com.impwrme2.model.resourceParam.ResourceParamType;

import jakarta.persistence.Convert;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;

@Entity
@DiscriminatorValue(ResourceParamType.Values.BIG_DECIMAL)
public class ResourceParamDateValueBigDecimal extends ResourceParamDateValue<BigDecimal> {

	private static final long serialVersionUID = -2447761790662733031L;

	protected ResourceParamDateValueBigDecimal() {
		super();
	}
	
	public ResourceParamDateValueBigDecimal(YearMonth yearMonth, boolean userAbleToChangeDate, final BigDecimal value) {
		super(yearMonth, userAbleToChangeDate, value);
	}

	@Convert(converter = BigDecimalStringAttributeConverter.class)
	private BigDecimal value;

	@Override
	@NotNull(message = "{msg.validation.resourceParamDateValue.value.notEmpty}")
	public BigDecimal getValue() {
		return this.value;
	}

	@Override
	public void setValue(BigDecimal value) {
		this.value = value;		
	}

	@Override
	public void setValueFromString(String value) {
		setValue(new BigDecimal(value));		
	}

//	@SuppressWarnings("unchecked")
//	@Override
//	public void setResourceParamGeneric(ResourceParam<?> resourceParam) {
//		this.setResourceParam((ResourceParam<BigDecimal>) resourceParam);
//	}
}
