package com.impwrme2.model.resourceParamDateValue;

import java.time.YearMonth;

import com.impwrme2.model.YearMonthStringAttributeConverter;
import com.impwrme2.model.resourceParam.ResourceParamType;
import com.impwrme2.utils.YearMonthUtils;

import jakarta.persistence.Convert;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;

@Entity
@DiscriminatorValue(ResourceParamType.Values.YEAR_MONTH)
public class ResourceParamDateValueYearMonth extends ResourceParamDateValue<YearMonth> {

	private static final long serialVersionUID = 8035216939414871982L;

	protected ResourceParamDateValueYearMonth() {
		super();
	}
	
	public ResourceParamDateValueYearMonth(YearMonth yearMonth, boolean userAbleToChangeDate, final YearMonth value) {
		super(yearMonth, userAbleToChangeDate, value);
	}

	@Convert(converter = YearMonthStringAttributeConverter.class)
	private YearMonth value;

	@Override
	@NotNull(message = "{msg.validation.resourceParamDateValue.value.notEmpty}")
	public YearMonth getValue() {
		return this.value;
	}

	@Override
	public void setValue(YearMonth value) {
		this.value = value;		
	}

	@Override
	public void setValueFromString(String value) {
		setValue(YearMonthUtils.getYearMonthFromStringInFormatMM_YYYY(value));		
	}
}
